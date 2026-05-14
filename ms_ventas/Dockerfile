# Imagen de Java 21 
FROM eclipse-temurin:21

# Directorio de trabajo 
WORKDIR /app

# Copiado y Guardado en el Contenedor
# Source / Dest
# Fuente / Destino
COPY target/*.jar app.jar

# Comando de Arranque (lo que se ejecuta en el contenedor de Docker)
ENTRYPOINT ["java", "-jar", "app.jar"]