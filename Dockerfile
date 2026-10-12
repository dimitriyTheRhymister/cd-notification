FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src

RUN mvn clean package -DskipTests

EXPOSE 9920
CMD ["java", "-jar", "target/notification-1.0.0.jar"]
