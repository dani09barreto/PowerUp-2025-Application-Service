# ==============================
# Stage 1: Build the application
# ==============================
FROM gradle:8.4-jdk21-alpine AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el código fuente al contenedor
COPY . .

# Compilamos el proyecto y generamos el .jar
RUN gradle clean bootJar -x validateStructure --no-daemon

# ==============================
# Stage 2: Run the application
# ==============================
FROM eclipse-temurin:21-jdk-alpine

# Directorio de trabajo
WORKDIR /app

# Creamos volumen temporal (por si la app lo necesita)
VOLUME /tmp

# Copiamos el jar generado en la etapa de build
COPY --from=build /app/applications/app-service/build/libs/*.jar app.jar

# Usuario no root por seguridad
RUN adduser -D appuser
USER appuser

EXPOSE 8081
# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]