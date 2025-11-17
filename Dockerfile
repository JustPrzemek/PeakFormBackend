# --- Etap 1: Budowanie aplikacji (Builder) ---
# Użyj oficjalnego obrazu dla Javy 21 (Maven do budowania)
FROM eclipse-temurin:21-jdk-jammy AS builder

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj pliki Mavena (wrapper i pom)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Pobierz zależności (dzięki temu warstwa ta będzie cache'owana)
RUN ./mvnw dependency:go-offline

# Skopiuj kod źródłowy
COPY src ./src

# Zbuduj aplikację, pomijając testy (dobra praktyka w CI/CD)
RUN ./mvnw clean package -DskipTests


# --- Etap 2: Obraz produkcyjny (Runner) ---
# Użyj mniejszego obrazu JRE (Java Runtime Environment) dla Javy 21
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Ustaw port, na którym Spring Boot domyślnie działa
# Render i tak użyje zmiennej $PORT, ale to dobra praktyka
EXPOSE 8080

# Skopiuj zbudowany plik .jar z etapu "builder"
COPY --from=builder /app/target/*.jar app.jar

# Komenda startowa
# Używamy portu podanego przez Render ($PORT)
ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT}", "app.jar"]