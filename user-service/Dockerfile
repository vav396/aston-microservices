FROM amazoncorretto:21

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.import=configserver:http://config-server:8888", "-jar", "app.jar"]
