FROM openjdk:19
EXPOSE 8080
EXPOSE 81
EXPOSE 82
EXPOSE 83
ADD target/authenticator.jar authenticator.jar
ENTRYPOINT ["java", "-jar", "/authenticator.jar"]