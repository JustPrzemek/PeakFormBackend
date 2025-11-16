package com.peakform.meals.weightlog.service;

import com.peakform.meals.weightlog.dto.WeightHistoryDto;
import com.peakform.meals.weightlog.dto.WeightLogDto;
import com.peakform.meals.weightlog.model.WeightLog;
import com.peakform.meals.weightlog.repository.WeightLogRepository;
import com.peakform.security.user.model.User;
import com.peakform.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WeightLogService {

    private final WeightLogRepository weightLogRepository;
    private final UserRepository userRepository;

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Zapisuje lub aktualizuje wagę użytkownika na DZISIEJSZĄ datę.
     */
    public void addOrUpdateWeight(WeightLogDto dto, String username) {
        User user = findUserByUsername(username);

        LocalDate today = LocalDate.now();

        WeightLog log = weightLogRepository.findByUserIdAndDate(user.getId(), today)
                .orElseGet(() -> {
                    WeightLog newLog = new WeightLog();
                    newLog.setUser(user);
                    newLog.setDate(today);
                    return newLog;
                });

        log.setWeight(dto.weight());

        weightLogRepository.save(log);
    }

    /**
     * Zwraca historię wagi dla użytkownika, posortowaną od najnowszej.
     */
    @Transactional(readOnly = true)
    public List<WeightHistoryDto> getWeightHistory(String username) {
        User user = findUserByUsername(username);

        List<WeightLog> logs = weightLogRepository.findByUserIdOrderByDateDesc(user.getId());

        return logs.stream()
                .map(log -> new WeightHistoryDto(log.getDate(), log.getWeight()))
                .collect(Collectors.toList());
    }
}
