# 1-bosqich: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# 2-bosqich: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# --- VAQT MINTAQASINI O'RNATISH (SHU QISMNI QO'SHING) ---
ENV TZ=Asia/Tashkent
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# -----------------------------------------------------

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/logs && chmod -R 777 /app/logs

EXPOSE 8080

# Ilovani ishga tushirish (JVM flagi bilan vaqtni kafolatlaymiz)
ENTRYPOINT ["java", "-Duser.timezone=Asia/Tashkent", "-jar", "app.jar"]
