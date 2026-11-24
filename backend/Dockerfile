FROM ubuntu:latest
LABEL authors="kulam"
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY Backend.java .
RUN javac Backend.java

EXPOSE 8081

CMD ["java", "Backend"]