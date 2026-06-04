# Etapa 1: Compilación
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos el pom.xml (Nuestra fuente de la verdad para Spring Boot 4.0.6)
COPY pom.xml .
# Descargamos dependencias para optimizar la caché de Docker
RUN mvn dependency:go-offline
# Copiamos el código fuente
COPY src ./src
# Empaquetamos ignorando los tests
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ultra-ligera de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos el .jar generado desde la etapa de compilación (usamos comodín)
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
# Exponemos el puerto estándar interno
EXPOSE 8080
# Ejecutamos el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]