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
 - Postgres database
 - H2 database (internal database used for tests only)

## Configuration files
```application.yml``` in both ```src/main/resources/``` and ```src/test/resources/application.yml``` contain configuration variables that may be replaced by global environment variables.


## Building the app

### Dockerized environment

There is a possibility to run MathShareBackend application in an environment based on Docker. This environment consists of two images:
 - server - MathShareBackend application
 - postgresql - PostgreSQL database image
 
Using this approach allows to run the application in an environment independent of a host. It simplifies a potential configuration and provides a possibilty to clean restart the application.

#### Windows
On Windows, there are two options for runninng Docker:
* based on Hyper-V (requires Windows 10 Pro or higher)
* based on Virtual Machine - installed by Docker Toolbox

Both requires BIOS supported virtualization. This guide has been prepared using Docker based on Virtual Machine version. It is important to notice that Docker for Windows based on Virtual Machine uses a **different IP** (eg. 192.168.99.100) so the MathShareBackend server application may be different.

You can get the IP by opening Docker Quickstart Terminal - the URL should be displayed as a welcome message.

### Without docker-compose

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

### With docker-compose

#### Running the environment

This command should be executed in the root project directory (a docker-compose.yml file should be visible there).

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

#### Copying artifact from container to the host

To get the built maven artifact from docker container run this:

```bash
docker cp "$(docker-compose ps -q server)":/mathshare/app.jar /location/of/output
```


### Docker variables

The properties (such as a database user login/password) are possible to be injected via a global environment variables functionality. A specification of using these properties in a dockerized environment may be found there: 
- https://github.com/benetech/MathShare/wiki/Configuration-variables---MathShareBackend
