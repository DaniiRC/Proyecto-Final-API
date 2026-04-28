# Fase de construccion: Se prepara la herramienta Maven con la version 21 de Java.
FROM maven:3.9.6-eclipse-temurin-21 AS etapa_empaquetado

# Transferencia de los archivos del proyecto desde el repositorio de GitHub.
COPY . .

# Ejecucion del comando manual para generar el archivo ejecutable omitiendo las pruebas.
RUN mvn clean package -DskipTests

# Fase de ejecucion: Se configura el entorno ligero de ejecucion para Java 21.
FROM eclipse-temurin:21-jdk-jammy

# Se copia el archivo compilado desde la etapa anterior para su puesta en marcha.
COPY --from=etapa_empaquetado /target/*.jar aplicacion_version_final.jar

# Declaracion del puerto de red para el trafico de la API.
EXPOSE 8080

# Instruccion de arranque del servidor de Spring Boot.
ENTRYPOINT ["java","-jar","/aplicacion_version_final.jar"]