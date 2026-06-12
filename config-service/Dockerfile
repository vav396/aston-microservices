FROM amazoncorretto:21

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
