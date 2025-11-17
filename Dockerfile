# ETAP 1: Budowanie aplikacji (Builder)
# Używamy oficjalnego obrazu Maven z JDK 21 do skompilowania kodu
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Kopiujemy plik pom.xml i pobieramy zależności
# Robimy to osobno, aby Docker mógł zbuforować (cache) tę warstwę
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Kopiujemy resztę kodu źródłowego i budujemy aplikację
COPY src ./src
# Używamy 'package' aby stworzyć .jar i '-DskipTests' aby przyspieszyć build
RUN mvn package -DskipTests

# ---

# ETAP 2: Obraz końcowy (Runner)
# Używamy lekkiego obrazu JRE, aby tylko uruchomić aplikację
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiujemy tylko gotowy plik .jar z etapu "builder"
# Zwróć uwagę na "--from=builder"
COPY --from=builder /workspace/target/peakform-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]