# Turning REST APIs to AI-Callable MCP Tools
### Building an MCP Server with Spring Boot and Spring AI

## Prerequisites
This project requires:

- [Java 21](https://sdkman.io/install/)
- MongoDB
- [Docker](https://www.docker.com)
- [Maven](https://maven.apache.org), Make sure environment variable `M2_REPO` is set to local maven repository
- IDE (IntelliJ, Eclipse or VS Code), Recommended [IntelliJ IDEA](https://www.jetbrains.com/idea).
- Optional [Configure Formatter in intelliJ](https://github.com/google/google-java-format/blob/master/README.md#intellij-android-studio-and-other-jetbrains-ides), refer to [fmt-maven-plugin](https://github.com/spotify/fmt-maven-plugin) for details.

**Recommended [sdkman](https://sdkman.io/install/) for managing Java, Scala and even Spark installations.**

## What is the Model Context Protocol (MCP) Server?
The [Model Context Protocol](https://modelcontextprotocol.io/introduction) (MCP) is an open-source standard developed by Anthropic that enables AI models to securely connect with external data sources and tools. 
Think of it as a universal translator that allows AI assistants to interact with your applications, databases, and services in a standardized way.  
MCP operates on a client-server architecture where AI applications act as MCP clients, connecting to MCP servers that provide access to specific resources or capabilities. 
This separation ensures security and modularity - servers can expose only the data and functions they choose to share.  
**The protocol can provide three main types of capabilities**: 
* **Resources**: File-like data that can be read by clients (like API responses or file contents)
* **Tools**: Functions that can be called by the LLM (with user approval)
* **Prompts**: Pre-written templates that help users do specific tasks

> [!IMPORTANT]
> The protocol's standardization means that once you build an MCP server, it can work with any MCP-compatible AI client, 
> creating an ecosystem of reusable integrations that reduce development overhead and improve interoperability.

## Mock Airline Rest Service
Spring boot project [**mock-airline-service**](mock-airline-service) provides a mock airline REST service that can be used to demonstrate the capabilities of an MCP server. 
It simulates a simple airline booking system with endpoints for searching flights, booking tickets and managing reservations. 
This service's REST APIs are registered as MCP tools, allowing AI applications to interact with it via MCP server.

### Following are the available Mock Airline APIs

#### Search Flights
To search available flights for given origin, destination and departure date.
```http
POST /v1/airline/flights
```
Example Request Curl:
```bash
{
  "origin": "DEL",
  "destination": "JFK",
  "departureDate": "2025-04-21"
}
```

#### Book Flight
Book a flight. Create a reservation for a flight with given flight number and passenger details.

```http
POST /v1/airline/bookings
```
Example Request Curl:
```bash
curl -X 'POST' \
  'http://localhost:8092/v1/airline/bookings' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "flightNumber": "60000000",
  "passengers": [
    {
      "firstName": "Rajveer",
      "lastName": "Singh",
      "type": "ADULT"
    },
    {
      "firstName": "Jordan",
      "lastName": "Singh",
      "type": "CHILD"
    }
  ]
}'
```

#### Get Booking details
Get a flight reservation details by PNR.

```http
POST /v1/airline/bookings/{pnr}
```

| Parameter | Type     | Description           | Required | Example  |
|:----------|:---------|:----------------------|:---------|:---------|
| `pnr`     | `String` | Passenger Name Record | Yes      | BA026    |

#### Add a Special service to a booking
Adds a Special service to an existing reservation.

```http
POST /v1/airline/bookings/{pnr}/services
```
Example Request Curl:
```bash
curl -X 'POST' \
  'http://localhost:8092/v1/airline/bookings/BA026/services' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "service": "BAG"
}'
```

| Parameter | Type     | Description           | Required | Example  |
|:----------|:---------|:----------------------|:---------|:---------|
| `pnr`     | `String` | Passenger Name Record | Yes      | BA026    |

#### Cancel a booking
Cancels an existing reservation by PNR.

```http
PUT /v1/airline/bookings/{pnr}/cancel
```
Example Request Curl:
```bash
curl -X 'PUT' \
  'http://localhost:8092/v1/airline/bookings/BA026/cancel' \
  -H 'accept: application/json'
```

| Parameter | Type     | Description           | Required | Example  |
|:----------|:---------|:----------------------|:---------|:---------|
| `pnr`     | `String` | Passenger Name Record | Yes      | BA026    |

### Running the Mock Airline Service
Run [**MockAirlineApplication**](mock-airline-service/src/main/java/com/ksoot/airline/MockAirlineApplication.java) as Spring boot application.
* Make sure MongoDB is running on `localhost:27017` or update the [application.yml](mock-airline-service/src/main/resources/config/application.yml) file with your MongoDB connection details.
* Application is bundled with [Docker Compose Support](https://www.baeldung.com/docker-compose-support-spring-boot), so if you don't have MongoDB running on your machine then you can run it in Docker by running the application in `docker` profile by setting VM option `-Dspring.profiles.active=docker`.
* Access Swagger UI at [http://localhost:8092/swagger-ui/index.html](http://localhost:8092/swagger-ui/index.html) to explore the available REST APIs.

## MCP server to turn REST APIs into AI-Callable Tools
Spring boot project [**rest-api-mcp-server**](rest-api-mcp-server) **implements the core idea to convert REST API operations into MCP Tools**.
Each tool is an object of class [**ApiToolCallback**](rest-api-mcp-server/src/main/java/ai/ksoot/rest/mcp/server/tool/domain/model/ApiToolCallback.java) and abstract the tool definition along with parameters and logic to execute the remote API call. 
A tool is defined by the following properties:
* [**ToolDefinition**](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/tool/support/ToolDefinitions.html): Definition used by the AI model to determine when and how to call the tool.
  * **Name***: The tool name. Unique within the tool set provided to a model.
  * **Description***: The tool description, used by the AI model to determine what the tool does.
  * **Input Schema***: The schema of the parameters used to call the tool.
> [!IMPORTANT]
> The tool definition is used by the AI model, so these attributes must be provided with values that assist the AI Agents in deciding when and how to use the respective tool.
* [ToolMetadata](https://docs.spring.io/spring-ai/reference/api/aimetadata.html): Metadata about a tool specification and execution.
    * **Return Direct**: Whether the tool result should be returned directly or passed back to the model.
* **httpMethod***: The HTTP method of Target REST API, such as `GET`, `POST`, `PUT`, etc.
* **defaultHeaders**: The static headers set while making the API call, such as `Content-Type`, `Accept`, etc.
* **baseUrl***: The Base URL of the Target REST API, such as `http://localhost:8092/v1/airline`.
* **path***: The path of the Target REST API. e.g. `/bookings/{pnr}`.
* **pathVariables**: The names of path variables to be set while making the API call.
* **queryParams**: The names of query parameters to be passed while making the API call.
* **headers**: The runtime headers to be set while making the API call
* **bodyArg**: The name of the body argument to be passed while making the API call.

> [!NOTE]
> The parameters marked with `*` are mandatory to define a tool. 
> The AI agent just needs to know the tool name, description and input schema and tool metadata to call it.
> MCP server uses The remaining parameters internally to make the actual API call. So the AI agent does not know how to make a REST API call but MCP server does.

### Creating MCP Tools for REST API calls
Either you can create MCP tools manually by implementing the defining a `Bean` of [**ApiToolCallback**](rest-api-mcp-server/src/main/java/ai/ksoot/rest/mcp/server/tool/domain/model/ApiToolCallback.java) as follows.
```java

```

### Following are the available MCP server APIs

#### Search Flights
To search available flights for given origin, destination and departure date.
```http
POST /v1/airline/flights
```
Example Request Curl:
```bash
{
  "origin": "DEL",
  "destination": "JFK",
  "departureDate": "2025-04-21"
}
```
