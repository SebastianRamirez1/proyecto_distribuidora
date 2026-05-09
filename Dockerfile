# ════════════════════════════════════════════════════════════
#  ETAPA 1 — Compilar frontend (Node 20)
# ════════════════════════════════════════════════════════════
FROM node:20-alpine AS frontend-build

WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# ════════════════════════════════════════════════════════════
#  ETAPA 2 — Compilar backend (Maven + Java 22)
# ════════════════════════════════════════════════════════════
FROM maven:3.9.6-eclipse-temurin-22 AS backend-build

WORKDIR /app

# Copiar pom.xml para cachear descarga de dependencias Java
COPY pom.xml ./

# Descargar solo dependencias Java (NO el frontend-maven-plugin)
# -DskipFrontend=true evita que Maven invoque Node/npm
RUN mvn dependency:resolve -q -DskipFrontend=true

# Copiar código fuente
COPY src/ ./src/

# Copiar el frontend ya compilado desde la etapa 1
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static/

# Empaquetar JAR. Saltamos tests y el plugin de frontend
RUN mvn package -DskipTests -DskipFrontend=true

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
