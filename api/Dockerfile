# Stage 1: Build da aplicacao usando o Maven Wrapper
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copia os arquivos de configuracao do Maven e o pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Garante permissao de execucao no script mvnw
RUN chmod +x mvnw

# Baixa as dependencias em cache (otimizacao para builds subsequentes)
RUN ./mvnw dependency:go-offline -B

# Copia o codigo-fonte e compila o JAR
COPY src ./src
RUN ./mvnw package -DskipTests -B

# Stage 2: Imagem leve de runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria um usuario nao-root por questoes de seguranca
RUN addgroup -S spring && adduser -S spring -G spring

# Garante que a pasta de uploads fisicos exista e pertença ao usuario spring
RUN mkdir -p uploads/products && chown -R spring:spring /app

USER spring:spring

# Copia o JAR do estagio anterior
COPY --from=builder /app/target/*.jar app.jar

# Define as variaveis de ambiente
ENV PORT=8080
EXPOSE ${PORT}

# Executa a aplicacao utilizando o profile de producao (prod)
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "-jar", "app.jar"]
