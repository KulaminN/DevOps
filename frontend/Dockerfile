FROM ubuntu:latest
LABEL authors="kulam"
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY Frontend.java .
RUN javac Frontend.java

COPY index.html .

EXPOSE 8080

CMD ["java", "Frontend"]