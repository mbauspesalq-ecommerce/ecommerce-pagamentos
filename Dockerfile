# Etapa de construção
FROM openjdk:17-jdk-slim as build

# Copiar o arquivo JAR gerado para o container
COPY target/pagamentos-*.jar /app/pagamentos.jar

# Etapa de execução
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar o arquivo JAR da etapa de construção
COPY --from=build /app/pagamentos.jar /app/pagamentos.jar

# Expor a porta da aplicação
EXPOSE 8001

# Comando para rodar a aplicação
CMD ["java", "-jar", "pagamentos.jar"]