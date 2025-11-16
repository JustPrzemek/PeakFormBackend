# ----- ETAP 1: Budowanie Aplikacji (Builder) -----
FROM maven:3.9-eclipse-temurin-21 AS build

# Ustawiamy katalog roboczy wewnątrz kontenera
WORKDIR /app

# Kopiujemy cały projekt
COPY . .

# Budujemy aplikację Mavenem, pomijając testy
# To stworzy plik /app/target/peakform-0.0.1-SNAPSHOT.jar
RUN mvn clean package -DskipTests

# ----- ETAP 2: Uruchomienie Aplikacji (Runner) -----
# Używamy lekkiego obrazu Javy 21 (tylko środowisko uruchomieniowe)
FROM eclipse-temurin:21-jre-alpine

# Ustawiamy katalog roboczy
WORKDIR /app

# Kopiujemy gotowy plik .jar z etapu "build"
COPY --from=build /app/target/peakform-0.0.1-SNAPSHOT.jar .

# Komenda, która uruchomi Twoją aplikację
ENTRYPOINT ["java", "-jar", "peakform-0.0.1-SNAPSHOT.jar"]