FROM openjdk:21-alpine

WORKDIR /app

COPY target/peakform-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]