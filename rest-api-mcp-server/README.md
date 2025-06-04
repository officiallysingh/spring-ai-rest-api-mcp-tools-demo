## Running MCP Server
Run [**RestApiMCPServer**](src/main/java/ai/ksoot/rest/mcp/server/RestApiMCPServer.java) as Spring boot application.
* Make sure MongoDB is running on `localhost:27017` or update the [application.yml](src/main/resources/config/application.yml) file with your MongoDB connection details.
* Application is bundled with [Docker Compose Support](https://www.baeldung.com/docker-compose-support-spring-boot), so if you don't have MongoDB running on your machine, then you can run it in Docker by running the application in `docker` profile by setting VM option `-Dspring.profiles.active=docker`.
* Access Swagger UI at [http://localhost:8090/swagger-ui/index.html](http://localhost:8090/swagger-ui/index.html) to explore the available REST APIs.
