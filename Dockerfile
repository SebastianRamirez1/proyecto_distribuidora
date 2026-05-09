# ════════════════════════════════════════════════════════════
#  ETAPA 1 — Compilar frontend (Node 20)
# ════════════════════════════════════════════════════════════
FROM node:20-alpine AS frontend-build

WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install --prefer-offline || npm install
COPY frontend/ ./
RUN npm run build

# ════════════════════════════════════════════════════════════
#  ETAPA 2 — Compilar backend (Maven + Java 22)
# ════════════════════════════════════════════════════════════
FROM maven:3.9.6-eclipse-temurin-22 AS backend-build

WORKDIR /app

# Copiar pom.xml y descargar dependencias (capa cacheada)
COPY pom.xml ./
RUN mvn dependency:go-offline -q -P dev

# Copiar código fuente
COPY src/ ./src/

# Copiar frontend compilado al directorio static de Spring Boot
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static/

# Compilar JAR (sin el plugin de frontend — ya lo copiamos arriba)
RUN mvn package -q -DskipTests -P dev

# ════════════════════════════════════════════════════════════
#  ETAPA 3 — Imagen final ligera (solo JRE 22)
# ════════════════════════════════════════════════════════════
FROM eclipse-temurin:22-jre-alpine

WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S distribuidora && adduser -S distribuidora -G distribuidora
USER distribuidora

COPY --from=backend-build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]
