FROM gradle:7.6-jdk17 as build
#
WORKDIR /app
#
COPY gradlew gradlew.bat /app/
COPY gradle /app/gradle
#
RUN chmod +x ./gradlew
#
COPY build.gradle settings.gradle /app/
COPY src /app/src
#
RUN ./gradlew clean build

#
FROM openjdk:17-slim
#
ENV SPRING_PROFILES_ACTIVE=production
#
USER 331
#
WORKDIR /app
#
COPY --from=build /app/build/libs/*.jar /app/soprafs24.jar
#
EXPOSE 8080
#
CMD ["java", "-jar", "/app/soprafs24.jar"]