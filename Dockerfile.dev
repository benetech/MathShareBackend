FROM adoptopenjdk/maven-openjdk10 as maven
LABEL maintainer="johnh@benetech.org"

COPY ./pom.xml ./pom.xml

# build all dependencies
RUN mvn dependency:go-offline -B

# copy your other files
COPY ./src ./src
COPY ./checkstyle ./checkstyle

# build for release
RUN mvn package

# our final base image
FROM openjdk:10-jre-slim

# set deployment directory
WORKDIR /mathshare

# copy over the built artifact from the maven image
COPY --from=maven target/mathshare-*.jar ./app.jar
