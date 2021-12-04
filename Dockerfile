FROM openjdk:12
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Busqueda.jar
ENTRYPOINT ["java","-jar","/Busqueda.jar"]