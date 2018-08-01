# MathShareBackend

This is a repository for MathShare backend server. MathShare is a step by step editor built on top of mathlive. The code is based on Java 10 so a proper configuration of SDK should be set.

## Tech stack
 - Java 10
 - Spring Boot Application - API built using Spring REST MVC
 - Maven
 - ORM: Hibernate - use Spring Data JPA
 - Database versioning: Flyway
 - Spring Boot Admin can be used for easily adding an administrative interface
 - Docker
 - Postgres

## Configuration files
```application.yml``` in both ```src/main/resources/``` and ```src/test/resources/application.yml``` contain configuration variables that may be replaced by global environment variables.


## Building the app
First, we need to run s PostgreSQL database, the most convenient way to run it is to use Docker.
The following command will create a Docker container with all properties configured (database and user).

```bash
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_USER=postgres -e POSTGRES_DB=mathshare -d postgres
```

We recommend using PostgreSQL 9.5.12.

After running the command ```bash  ./mvnw clean install ``` (for Unix systems) or ```bash  ./mvnw.cmd clean install ``` (for Batch) the application will be built and then can be run with java.
You could also build and run the app from your favourite IDE, we recommend IntelliJ IDEA.
It is recommended to use pgadmin4 to connect to PostgreSQL database.
