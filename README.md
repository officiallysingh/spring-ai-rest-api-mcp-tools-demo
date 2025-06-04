# Turning REST APIs to AI-Callable MCP Tools
[![Java](https://img.shields.io/badge/java-21-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Java](https://img.shields.io/badge/spring_boot-3.4.4-blue.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/maven-3.9.5-blue.svg)](https://maven.apache.org/)
[![MongoDB](https://img.shields.io/badge/mongodb-6.0.6-blue.svg)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/docker-20.10.24-blue.svg)](https://www.docker.com/)
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

### Running the Mock Airline Service
Run [**MockAirlineApplication**](mock-airline-service/src/main/java/com/ksoot/airline/MockAirlineApplication.java) as Spring boot application.
* Make sure MongoDB is running on `localhost:27017` or update the [application.yml](mock-airline-service/src/main/resources/config/application.yml) file with your MongoDB connection details.
* Application is bundled with [Docker Compose Support](https://www.baeldung.com/docker-compose-support-spring-boot), so if you don't have MongoDB running on your machine then you can run it in Docker by running the application in `docker` profile by setting VM option `-Dspring.profiles.active=docker`.
* Access Swagger UI at [http://localhost:8092/swagger-ui/index.html](http://localhost:8092/swagger-ui/index.html) to explore the available REST APIs.

### Following are the available Mock Airline APIs

#### Search Flights
To search available flights for given origin, destination and departure date.
```http
POST /v1/airline/flights
```
Example Request Curl:
```bash
curl -X 'POST' \
  'http://localhost:8092/v1/airline/flights' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "origin": "DEL",
  "destination": "JFK",
  "departureDate": "2025-04-21"
}'
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

## MCP server to turn REST APIs into AI-Callable Tools
Spring boot project [**rest-api-mcp-server**](rest-api-mcp-server) **implements the core idea to convert REST API operations into MCP Tools** using [Spring AI](https://spring.io/projects/spring-ai) [MCP support](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html).
Each tool is an object of class [**ApiToolCallback**](rest-api-mcp-server/src/main/java/ai/ksoot/rest/mcp/server/tool/domain/model/ApiToolCallback.java) and abstract the tool definition along with parameters and logic to execute the remote API call. 
A tool is defined by the following properties:
* [**ToolDefinition**](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/tool/support/ToolDefinitions.html) ⭑ : Definition used by the AI model to determine when and how to call the tool.
  * **Name** ⭑ : The tool name. Unique within the tool set provided to a model.
  * **Description** ⭑ : The tool description, used by the AI model to determine what the tool does.
  * **Input Schema** ⭑ : The schema of the parameters used to call the tool.
* [ToolMetadata](https://docs.spring.io/spring-ai/reference/api/aimetadata.html): Metadata about a tool specification and execution.
  Currently, [DefaultToolMetadata](https://docs.spring.io/spring-ai/docs/1.0.0/api/org/springframework/ai/tool/metadata/DefaultToolMetadata.html) is used to define the metadata of a tool, but can be changed as per requirements.
    * **Return Direct**: Whether the tool result should be returned directly or passed back to the model.
> [!IMPORTANT]
> The AI model uses The tool definition and metadata while deciding when and how to use the respective tool, so these attributes must be provided with values that can assist the AI Agents.
* [**ToolCallResultConverter**](https://docs.spring.io/spring-ai/docs/1.0.x/api/org/springframework/ai/tool/execution/ToolCallResultConverter.html): Given an Object returned by a tool, convert it to a String compatible with the given class type.
  Currently, The default converter is [**DefaultToolCallResultConverter**](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/tool/converter/DefaultToolCallResultConverter.html) being used, but can be changed as per requirements.
* **Http Method** ⭑ : The HTTP method of Target REST API, such as `GET`, `POST`, `PUT`, etc.
* **Default Headers** ⭑ : The static header values set while making the API call, such as `Content-Type: application/json`, `Accept: */*`, etc.
* **Base Url** ⭑ : The Base URL of the Target REST API, such as `http://localhost:8092/v1/airline`.
* **Path** ⭑ : The path of the Target REST API. e.g. `/bookings/{pnr}`.
* **Path Variables**: The names of path variables to be set while making the API call. Not required by create Api Tool API, but automatically extracted from give `path`.
* **Query Parameters**: The names of query parameters to be passed while making the API call.
* **Headers**: The runtime headers to be set while making the API call.
* **Body Argument name**: The name of the body argument to be passed while making the API call. Cannot be used with `GET` method.

> [!NOTE]
> The parameters marked with ⭑ are mandatory to define a tool. 
> The AI agent just needs to know the tool name, description and input schema and tool metadata to call it.
> MCP server uses The remaining parameters internally to make the actual API call. So the AI agent does not know how to make a REST API call but MCP server does.

### Running MCP Server
Run [**RestApiMCPServer**](rest-api-mcp-server/src/main/java/ai/ksoot/rest/mcp/server/RestApiMCPServer.java) as Spring boot application.
* Make sure MongoDB is running on `localhost:27017` or update the [application.yml](mock-airline-service/src/main/resources/config/application.yml) file with your MongoDB connection details.
* Application is bundled with [Docker Compose Support](https://www.baeldung.com/docker-compose-support-spring-boot), so if you don't have MongoDB running on your machine then you can run it in Docker by running the application in `docker` profile by setting VM option `-Dspring.profiles.active=docker`.
* Access Swagger UI at [http://localhost:8090/swagger-ui/index.html](http://localhost:8090/swagger-ui/index.html) to explore the available REST APIs.

![MCP Server Swagger](https://github.com/officiallysingh/spring-ai-rest-api-mcp-tools-demo/blob/main/img/Swagger_MCP_Server.png)

### Creating MCP Tools for REST API calls

#### Programmatic Approach
Either you can create MCP tools manually by implementing by defining a `Bean` of type [**ApiToolCallback**](rest-api-mcp-server/src/main/java/ai/ksoot/rest/mcp/server/tool/domain/model/ApiToolCallback.java) as follows.
```java
  @Bean
  ApiToolCallback searchFlightApiTool() {
    final String inputSchema = """
            {
              "$schema" : "https://json-schema.org/draft/2020-12/schema",
              "type" : "object",
              "properties" : {
                "airShopRequest" : {
                  "$defs" : {
                    "Airport" : {
                      "type" : "string",
                      "enum" : [ "JFK", "LAX", "DEL", "BOM" ],
                      "description" : "Airport details"
                    }
                  },
                  "type" : "object",
                  "properties" : {
                    "departureDate" : {
                      "type" : "string",
                      "format" : "date",
                      "description" : "Departure Date"
                    },
                    "destination" : {
                      "$ref" : "#/$defs/Airport",
                      "description" : "Arrival airport"
                    },
                    "origin" : {
                      "$ref" : "#/$defs/Airport",
                      "description" : "Departure airport"
                    }
                  },
                  "required" : [ "departureDate", "destination", "origin" ],
                  "description" : "Airshop request to search flights"
                }
              },
              "required" : [ "airShopRequest" ],
              "additionalProperties" : false
            }
            """;
    
    final ApiToolCallback apiTool =
        ApiToolCallback.tool(
                "search_flight",
                "Search flights by origin, departure location and departure date",
                inputSchema)
            .baseUrl("http://localhost:8092")
            .path("/v1/airline/flights")
            .httpMethod(HttpMethod.POST)
            .bodyArg("airShopRequest")
            .build();
    return apiTool;
  }
```
Or a Function call Tool can be defined as follows:
```java
@Bean
public ToolCallback toUpperCase() {
  return FunctionToolCallback.builder(
          "toUpperCase", (TextInput input) -> input.input().toUpperCase())
      .inputType(TextInput.class)
      .description("Put the text to upper case")
      .build();
}
```

But it is recommended to create MCP tools for REST API calls using APIs provided as follows.
> [!IMPORTANT]
> The simplest way to create MCP tools for REST API calls is to upload the OpenAPI spec and take the response and parse to another API to create all tools at once. 
> The available APIs are listed below.

### Following are the available MCP server APIs

#### Create API Tool
Create an API tool by providing tool parameters.
```http
POST /v1/mcp/api-tools
```
Example Request Curl:
```bash
curl -X 'POST' \
  'http://localhost:8090/v1/mcp/api-tools' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '  {
    "name": "search-flights",
    "description": "Search available flights as per simple search criteria.",
    "inputSchema": "{\n  \"$schema\" : \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\" : \"object\",\n  \"properties\" : {\n    \"airShopRequest\" : {\n      \"type\" : \"object\",\n      \"description\" : \"Search Flight request\",\n      \"properties\" : {\n        \"origin\" : {\n          \"type\" : \"string\",\n          \"description\" : \"Departure airport\",\n          \"enum\" : [ \"JFK\", \"LAX\", \"DEL\", \"BOM\" ],\n          \"example\" : \"DEL\"\n        },\n        \"destination\" : {\n          \"type\" : \"string\",\n          \"description\" : \"Arrival airport\",\n          \"enum\" : [ \"JFK\", \"LAX\", \"DEL\", \"BOM\" ],\n          \"example\" : \"JFK\"\n        },\n        \"departureDate\" : {\n          \"type\" : \"string\",\n          \"format\" : \"date\",\n          \"description\" : \"Departure Date\",\n          \"example\" : \"2025-04-21\"\n        }\n      },\n      \"required\" : [ \"departureDate\", \"destination\", \"origin\" ]\n    }\n  },\n  \"required\" : [ \"airShopRequest\" ],\n  \"additionalProperties\" : false\n}",
    "returnDirect": false,
    "baseUrl": "http://localhost:8092",
    "httpMethod": "POST",
    "defaultHeaders": {
      "Accept": [
        "application/json"
      ],
      "Content-Type": [
        "application/json"
      ]
    },
    "path": "/v1/airline/flights",
    "bodyArg": "airShopRequest"
  }'
```

#### Get an API Tool by Id
To search available flights for given origin, destination and departure date.
```http
GET /v1/mcp/api-tools/{id}
```
Example Request Curl:
```bash
curl -X 'GET' \
  'http://localhost:8090/v1/mcp/api-tools/68106d632e1b8a3710692a85' \
  -H 'accept: application/json'
```

| Parameter | Type     | Description | Required | Example                  |
|:----------|:---------|:------------|:---------|:-------------------------|
| `id`      | `String` | API Tool Id | Yes      | 68106d632e1b8a3710692a85 |

#### Get an API Tool by Name
To search available flights for given origin, destination and departure date.
```http
GET /v1/mcp/api-tools/name/{name}
```
Example Request Curl:
```bash
curl -X 'GET' \
  'http://localhost:8090/v1/mcp/api-tools/name/search-flights' \
  -H 'accept: application/json'
```

| Parameter | Type     | Description   | Required | Example        |
|:----------|:---------|:--------------|:---------|:---------------|
| `name`    | `String` | API Tool name | Yes      | search-flights |

#### List API Tools - Paginated
Get a page of API Tools.
```http
GET http://localhost:8090/v1/mcp/api-tools
```
Example Request Curl:
```bash
curl -X 'GET' \
  'http://localhost:8090/v1/mcp/api-tools?page=0&size=16' \
  -H 'accept: application/json'
```

| Parameter | Type      | Description                                            | Required | Example |
|:----------|:----------|:-------------------------------------------------------|:---------|:--------|
| `page`    | `Integer` | Page index starting from 0                             | No       | 0       |
| `size`    | `Integer` | Page size, should be a whole number, default value: 16 | No       | 0       |


#### Parse OpenAPI Spec
Upload OpenAPI Specification in `json` or `yml` format to get API Tool definitions.
```http
POST http://localhost:8090/v1/mcp/api-tools/parse-openapi-spec
```
Example Request Curl:
```bash
curl -X 'POST' \
  'http://localhost:8090/v1/mcp/api-tools/parse-openapi-spec' \
  -H 'accept: application/json' \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@airline.json;type=application/json'
```

Upload OpenAPI spec file [airline.json](rest-api-mcp-server/src/main/resources/oas/airline.json) for Mock Airline REST server.  
You will get a response [airline-api-tools.json](rest-api-mcp-server/src/main/resources/oas/ailine-api-tools.json) with all the API tools derived from the APIs defined in the OpenAPI spec.  
You can download the response `json` from Swagger response of this API call.

#### Create API Tools from OpenAPI Spec
Copy the response from the above API call and pass it in body of following API to create all the API tools at once.
```http
POST http://localhost:8090/v1/mcp/api-tools/all
```
> [!IMPORTANT]
> The Parsed OpenAPI spec response may not contain a good enough Tool name and description to be useful for AI agent. 
> So you may need to update the Tool name and description in the response before passing it to this API.
> Also, some tools with given name may already exist, so you may need to change the names or remove those tools from the request to this API.

#### Other APIs
You can also use the following APIs to manage API tools:
* **List All API Tools**: Get a list of all API tools. Not recommended for too large number of tools.
```http
GET http://localhost:8090/v1/mcp/api-tools/all
```
* **Update API Tool by Id**: Update an existing API tool identifiable by given Id by providing tool parameters to be updated in body.
```http
PATCH http://localhost:8090/v1/mcp/api-tools/{id}
```
* **Update API Tool by Name**: Update an existing API tool identifiable by given Name by providing tool parameters to be updated in body.
```http
PATCH http://localhost:8090/v1/mcp/api-tools/name/{name}
```
* **Delete API Tool by Id**: Delete an existing API tool by providing tool id.
```http
DELETE http://localhost:8090/v1/mcp/api-tools/{id}
```
* **Delete API Tool by Name**: Delete an existing API tool by providing tool name.
```http
DELETE http://localhost:8090/v1/mcp/api-tools/name/{name}
```
* **Check if API Tool exists by name**: Check if an API tool exists by providing tool name.
```http
GET http://localhost:8090/v1/mcp/api-tools/name/{name}/exists
```

## Chat client to interact with AI Model and MCP server
Spring boot project [**airline-chat-bot**](airline-chat-bot) implements the AI Chat client for communicating with an AI Model and MCP Server to assist Mock Airline using natural language.
It is implemented using [Spring AI](https://spring.io/projects/spring-ai) [Chat Client API](https://docs.spring.io/spring-ai/reference/api/chatclient.html).

Run [**AirlineChatClientApplication**](airline-chat-bot/src/main/java/ai/ksoot/mcp/airline/client/AirlineChatClientApplication.java) as Spring boot application and 
Access Swagger UI at [http://localhost:8091/swagger-ui/index.html](http://localhost:8091/swagger-ui/index.html) to explore the available REST APIs.
