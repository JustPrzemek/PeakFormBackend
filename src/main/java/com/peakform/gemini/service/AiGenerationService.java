package com.peakform.gemini.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peakform.gemini.dto.AiPlanResponseDto;
import com.peakform.gemini.dto.GeminiRequestDto;
import com.peakform.gemini.dto.GeminiResponseDto;
import com.peakform.security.user.model.User;
import com.peakform.trainings.exercises.model.Exercises;
import com.peakform.trainings.exercises.repository.ExercisesRepository;
import com.peakform.trainings.workoutplanexercises.model.WorkoutPlanExercises;
import com.peakform.trainings.workoutplanexercises.repository.WorkoutPlanExerciseRepository;
import com.peakform.trainings.workoutplans.dto.PlanGenerationRequestDto;
import com.peakform.trainings.workoutplans.model.WorkoutPlans;
import com.peakform.trainings.workoutplans.repository.WorkoutPlansRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiGenerationService {

    private final ExercisesRepository exercisesRepository;
    private final WorkoutPlansRepository workoutPlanRepository;
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // Spring Boot automatycznie dostarcza ten Bean

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Transactional
    public WorkoutPlans generatePlan(User user, PlanGenerationRequestDto request) {
        // 1. Pobierz ćwiczenia z bazy, aby AI wiedziała, co ma do dyspozycji
        List<Exercises> availableExercises = exercisesRepository.findAll();

        // 2. Zbuduj prompt
        String prompt = buildPrompt(user, request, availableExercises);

        // 3. Wywołaj API Gemini
        String aiResponseJson = callGeminiApi(prompt);

        // 4. Sparsuj odpowiedź z AI do naszego DTO
        AiPlanResponseDto aiPlan = parseAiResponse(aiResponseJson);

        // 5. Zapisz plan w naszej bazie danych
        return savePlanToDatabase(user, aiPlan, request.getGoal());
    }

    private String buildPrompt(User user, PlanGenerationRequestDto request, List<Exercises> exercises) {
        // Konwertujemy listę ćwiczeń z Javy na string JSON, który AI zrozumie
        String exercisesJson;
        try {
            // Mapujemy tylko te pola, które AI potrzebuje, żeby nie wysyłać za dużo danych
            var exercisesSimple = exercises.stream()
                    .map(e -> new ExercisePromptDto(e.getId(), e.getName(), e.getType(), e.getMuscleGroup()))
                    .collect(Collectors.toList());
            exercisesJson = objectMapper.writeValueAsString(exercisesSimple);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Błąd serializacji ćwiczeń do JSON", e);
        }

        // Budujemy instrukcję dla AI
        return String.format(
                """
                Jesteś ekspertem-trenerem personalnym. Twoim zadaniem jest wygenerowanie spersonalizowanego planu treningowego.
                
                Oto dane użytkownika:
                - Cel: %s
                - Doświadczenie: %s
                - Ilość dni treningowych w tygodniu: %d
                - Płeć: %s
                - Waga: %.1f kg
                - Wzrost: %.1f cm
                
                Oto lista ćwiczeń, których MUSISZ użyć (używaj tylko i wyłącznie ćwiczeń z tej listy, podając ich "id"):
                %s
                
                Zasady:
                1. Dobierz odpowiednie ćwiczenia do celu (%s) i poziomu (%s).
                2. Dla ćwiczeń typu "STRENGTH" dobierz "sets", "reps", "restTime".
                
                --- NOWE WAŻNE ZASADY ---
                3. KRYTYCZNE: Wartość "reps" MUSI być liczbą całkowitą (np. 10). NIGDY nie używaj tekstów typu "AMRAP" ani zakresów "10-12". Zawsze podaj jedną, konkretną liczbę powtórzeń.
                4. KRYTYCZNE: Upewnij się, że wygenerowany JSON jest absolutnie poprawny, bez żadnych dodatkowych spacji w kluczach (np. "reps" jest poprawne, " "reps" jest błędne).
                --- KONIEC NOWYCH ZASAD ---
                
                5. Dla ćwiczeń typu "CARDIO" dobierz "durationMinutes" i opcjonalnie "distanceKm".
                6. Plan musi zawierać dokładnie %d dni treningowych. Nazwij je (np. A, B, C lub Push, Pull, Legs).
                
                Zwróć odpowiedź WYŁĄCZNIE jako obiekt JSON, bez żadnego dodatkowego tekstu ani formatowania markdown.
                Struktura JSON musi być następująca:
                
                {
                  "planName": "Kreatywna nazwa planu (max 25 znaków)",
                  "description": "Krótki opis planu i motywacja (max 1000 znaków).",
                  "days": [
                    {
                      "dayIdentifier": "A",
                      "exercises": [
                        { "exerciseId": 1, "sets": 3, "reps": 12, "restTime": 60 },
                        { "exerciseId": 2, "sets": 3, "reps": 10, "restTime": 90 },
                        { "exerciseId": 26, "durationMinutes": 20 }
                      ]
                    },
                    {
                      "dayIdentifier": "B",
                      "exercises": [ ... ]
                    }
                  ]
                }
                """,
                request.getGoal(), request.getExperience(), request.getDaysPerWeek(),
                user.getGender(), user.getWeight(), user.getHeight(),
                exercisesJson,
                request.getGoal(), request.getExperience(),
                request.getDaysPerWeek()
        );
    }

    private String callGeminiApi(String prompt) {
        String url = geminiApiUrl + "?key=" + geminiApiKey;

        // Budujemy ciało zapytania
        var part = new GeminiRequestDto.Part(prompt);
        var content = new GeminiRequestDto.Content(List.of(part));
        var requestBody = new GeminiRequestDto(List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiRequestDto> entity = new HttpEntity<>(requestBody, headers);

        try {
            GeminiResponseDto response = restTemplate.postForObject(url, entity, GeminiResponseDto.class);
            if (response == null) {
                throw new RuntimeException("API Gemini zwróciło pustą odpowiedź.");
            }
            return response.getFirstPartText();
        } catch (Exception e) {
            // Tutaj obsłuż błędy API (np. przekroczony limit, zły klucz)
            throw new RuntimeException("Błąd podczas wywołania API Gemini: " + e.getMessage(), e);
        }
    }

    private AiPlanResponseDto parseAiResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, AiPlanResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Błąd parsowania odpowiedzi JSON z AI. Odpowiedź: " + jsonResponse, e);
        }
    }

    @Transactional
    public WorkoutPlans savePlanToDatabase(User user, AiPlanResponseDto aiPlan, String goal) {
        // 1. Zapisz główny plan
        WorkoutPlans plan = new WorkoutPlans();
        plan.setUser(user);
        plan.setName(aiPlan.getPlanName());
        plan.setDescription(aiPlan.getDescription());
        plan.setGoal(goal); // Zapisujemy cel z requestu
        plan.setCreatedAt(LocalDateTime.now());
        WorkoutPlans savedPlan = workoutPlanRepository.save(plan);

        // 2. Zapisz poszczególne dni i ćwiczenia
        for (AiPlanResponseDto.AiDay day : aiPlan.getDays()) {
            for (AiPlanResponseDto.AiExercise aiEx : day.getExercises()) {
                // Musimy pobrać pełną encję ćwiczenia z naszej bazy
                Exercises exercise = exercisesRepository.findById(aiEx.getExerciseId())
                        .orElseThrow(() -> new RuntimeException("AI zwróciło nieistniejące ćwiczenie o ID: " + aiEx.getExerciseId()));

                WorkoutPlanExercises wpe = new WorkoutPlanExercises();
                wpe.setWorkoutPlans(savedPlan);
                wpe.setExercises(exercise);
                wpe.setDayIdentifier(day.getDayIdentifier());

                // Zapisujemy parametry w zależności od typu
                if ("STRENGTH".equalsIgnoreCase(exercise.getType())) {
                    wpe.setSets(aiEx.getSets());
                    wpe.setReps(aiEx.getReps());
                    wpe.setRestTime(aiEx.getRestTime());
                } else { // CARDIO
                    wpe.setDurationMinutes(aiEx.getDurationMinutes());
                    wpe.setDistanceKm(aiEx.getDistanceKm());
                }
                workoutPlanExerciseRepository.save(wpe);
            }
        }
        return savedPlan;
    }

    // Mała klasa pomocnicza do budowania promptu
    @Data
    @AllArgsConstructor
    private static class ExercisePromptDto {
        private Long id;
        private String name;
        private String type;
        private String muscleGroup;
    }
}
