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

First, we need to run a PostgreSQL database, the most convenient way to run it is to use Docker.
The following command will create a Docker container with all properties configured (database and user).

##### DB initialization

```bash
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_USER=postgres -e POSTGRES_DB=mathshare -d postgres
```

##### Next DB runs

```bash
docker stop postgres && docker start postgres
```

We recommend using PostgreSQL 9.5.12.

After running the command ```bash  ./mvnw clean install ``` (for Unix systems) or ```bash  ./mvnw.cmd clean install ``` (for Batch) the application will be built and then can be run with java.
You could also build and run the app from your favourite IDE, we recommend IntelliJ IDEA.
It is recommended to use pgadmin4 to connect to PostgreSQL database.

## Dockerized environment

There is a possibility to run MathShareBackend application in an environment based on Docker. This environment consists of two images:
 - server - MathShareBackend application
 - postgresql - PostgreSQL database image
 
Using this approach allows to run the application in an environment independent of a host. It simplifies a potential configuration and provides a possibilty to clean restart the application.
 
Currently, there is a need to build an application image using PostgreSQL database (the test are executed while build).
If a database **is not running** you can execute the following command:

```bash
docker stop postgres && docker start postgres
```

### Building a docker image

To build an image run the command below in the root project directory:

```bash
mvn clean package docker:build
```

### Running the environment

If the process completes with success, a new docker image should be created. Now it is possible to run the whole docker environment (the command should be executed in the root project directory - a docker-compose.yml file should be visible there).

```bash
docker-compose up
```

In general application development a common case for the next runs is that the docker containers should be removed and the docker images should be started again.

```bash
docker-compose down && docker-compose up
```

Optionally, you can also remove the non-volatile database storage by removing the related docker volumes. This may be executed by the following command.

```bash
docker-compose down -v && docker-compose up
```

### Docker variables

The properties (such as a database user login/password) are possible to be injected via a global environment variables functionality. A specification of using these properties in a dockerized environment may be found there: 
- https://github.com/benetech/MathShare/wiki/Configuration-variables---MathShareBackend
