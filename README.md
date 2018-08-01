# MathShareBackend

This is a repository for MathShare backend server. The code is based on Java 10 so a proper configuration of SDK should be set.

## PostgreSQL
The most convenient way to run PostgreSQL database is to use Docker. The following command will create a Docker container with all properties configured (database and user).

```bash
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_USER=postgres -e POSTGRES_DB=mathshare -d postgres
``` 
After running the command the application should be able to start with success.
It is recommended to use pgadmin4 to connect to PostgreSQL database.
