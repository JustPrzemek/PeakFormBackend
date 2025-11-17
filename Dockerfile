# Używamy oficjalnego obrazu Adoptium Temurin (następcy OpenJDK)
FROM eclipse-temurin:21-jre-alpine

# Ustawiamy katalog roboczy
WORKDIR /app

# Kopiujemy zbudowany plik .jar do obrazu
# Zakładamy, że .jar jest w katalogu 'target' w głównym folderze projektu
COPY target/peakform-0.0.1-SNAPSHOT.jar app.jar

# Informujemy Docker, że aplikacja będzie działać na porcie 8080
EXPOSE 8080

# Polecenie uruchamiające aplikację
CMD ["java", "-jar", "app.jar"]