FROM openjdk:10
LABEL maintainer="johnh@benetech.org"
VOLUME /tmp
ADD target/mathshare*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 8080
