# ---------- Build Stage ----------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy gradle wrapper & configuration
COPY gradlew build.gradle settings.gradle ./
COPY gradle gradle
RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy project source
COPY src src

# Build application
RUN ./gradlew clean build -x test --no-daemon

# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# HEROKU PORT FIX — must use shell form
CMD java -Dspring.profiles.active=dev -Dserver.port=$PORT -jar app.jar
