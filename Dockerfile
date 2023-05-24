FROM eclipse-temurin
RUN mkdir /opt/app
COPY target/homeservice-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-jar", "/opt/app/homeservice-0.0.1-SNAPSHOT.jar"]