# ════════════════════════════════════════════════════════════
#  ETAPA 1 — Compilar frontend (Node 20 Alpine)
# ════════════════════════════════════════════════════════════
FROM node:20-alpine AS frontend-build

WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# ════════════════════════════════════════════════════════════
#  ETAPA 2 — Compilar backend (Java 21 LTS + Maven)
# ════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jdk-alpine AS backend-build

WORKDIR /app

# Instalar Maven en Alpine
RUN apk add --no-cache curl && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz \
    | tar -xzC /opt && \
    ln -s /opt/apache-maven-3.9.6/bin/mvn /usr/local/bin/mvn

# Cachear dependencias Java primero
COPY pom.xml ./
RUN mvn dependency:resolve -q -DskipFrontend=true

# Copiar fuentes y frontend compilado
COPY src/ ./src/
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static/

# Compilar JAR (sin tests, sin plugin de Node)
RUN mvn package -DskipTests -DskipFrontend=true

# ════════════════════════════════════════════════════════════
#  ETAPA 3 — Imagen final mínima (solo JRE 21)
# ════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S distribuidora && adduser -S distribuidora -G distribuidora
USER distribuidora

COPY --from=backend-build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]
