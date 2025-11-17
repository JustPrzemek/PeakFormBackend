# Użyj obrazu bazowego z JDK 17 (lub 21, jeśli używasz)
FROM eclipse-temurin:21-jdk-jammy

# Ustaw katalog roboczy w kontenerze
WORKDIR /app

# Skopiuj pliki build (Maven wrapper)
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Pobierz zależności (dzięki temu warstwa będzie cache'owana)
RUN ./mvnw dependency:go-offline

# Skopiuj resztę kodu źródłowego
COPY src ./src

# Zbuduj aplikację (tworząc plik JAR)
RUN ./mvnw clean package -DskipTests

# Użyj lżejszego obrazu do uruchomienia
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Skopiuj plik JAR z poprzedniego etapu
COPY --from=0 /app/target/*.jar app.jar

# Ustaw port, na którym będzie działać aplikacja
EXPOSE 8080

# Komenda uruchomieniowa
ENTRYPOINT ["java", "-jar", "app.jar"]