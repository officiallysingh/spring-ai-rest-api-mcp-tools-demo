# Turning REST APIs to AI-Callable MCP Tools
### Building an MCP Server with Spring Boot and Spring AI

![MCP Overview](https://github.com/officiallysingh/spring-ai-rest-api-mcp-tools-demo/blob/main/img/MCP_Overview.png)

## Prerequisites

[![Java](https://img.shields.io/badge/java-21-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring_boot-3.4.5-blue.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/spring_ai-1.0.0-blue.svg)](https://spring.io/projects/spring-ai)
[![Model Context Protocol](https://img.shields.io/badge/mcp-0.8.x-blue.svg)](https://modelcontextprotocol.io/sdk/java/mcp-overview)
[![Maven](https://img.shields.io/badge/maven-3.9.5-blue.svg)](https://maven.apache.org/)
[![MongoDB](https://img.shields.io/badge/mongodb-6.0.6-blue.svg)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/docker-20.10.24-blue.svg)](https://www.docker.com/)

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
* Application is bundled with [Docker Compose Support](https://www.baeldung.com/docker-compose-support-spring-boot), so if you don't have MongoDB running on your machine, then you can run it in Docker by running the application in `docker` profile by setting VM option `-Dspring.profiles.active=docker`.
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
  * **Input Schema** ⭑ : The [json schema](https://json-schema.org/learn) of the parameters used to call the tool.
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
> The parameters marked with ⭑ are mandatory to define an API tool. 
> The AI agent just needs to know the tool name, description and input schema and tool metadata to call it.
> MCP server uses the remaining parameters internally to make the actual API call. So the AI agent does not know how to make a REST API call but MCP server does.

### Running MCP Server
Run [**RestApiMCPServer**](rest-api-mcp-server/src/main/java/ai/ksoot/rest/mcp/server/RestApiMCPServer.java) as Spring boot application.
* Make sure MongoDB is running on `localhost:27017` or update the [application.yml](mock-airline-service/src/main/resources/config/application.yml) file with your MongoDB connection details.
* Application is bundled with [Docker Compose Support](https://www.baeldung.com/docker-compose-support-spring-boot), so if you don't have MongoDB running on your machine, then you can run it in Docker by running the application in `docker` profile by setting VM option `-Dspring.profiles.active=docker`.
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

**But it is recommended to create MCP tools for REST API calls using APIs provided as follows.**
> [!IMPORTANT]
> The simplest way to create MCP tools for REST API calls is to upload the OpenAPI spec and take the response and pass to another API to create all tools at once. 
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
Find an API tool by given name.
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

> [!IMPORTANT]
> The most complex part is creating input schema for the API tool, which is automatically generated from the OpenAPI spec.

#### Create API Tools from OpenAPI Spec
Copy the response from the above API call and pass it in body of following API to create all the API tools at once.
```http
POST http://localhost:8090/v1/mcp/api-tools/all
```
> [!IMPORTANT]
> The Parsed OpenAPI spec response may not contain a good enough Tool name and description to be useful for AI agent. 
> So you may need to update the Tool name and description in request before passing it to this API.
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
* **Audit History Log**: Get the audit history log of API tools created, updated or deleted and by whom.
```http
GET http://localhost:8090/v1/mcp/api-tools/audit-history
```

### Tool Changes notifications
The MCP server supports [Server-Sent Events (SSE)](https://spring.io/blog/2017/03/08/spring-tips-server-sent-events-sse) to notify clients about changes in API tools as per protocol. 
[ToolChangeListener](rest-api-mcp-server/src/main/java/ai/ksoot/rest/mcp/server/tool/domain/service/ToolChangeListener.java) handles the tool change events and notifies the clients subscribed to the SSE endpoint. 
So if appropriately handled by the clients, any API Tool changes such as creation, update or deletion are notified to the clients in real-time and client can immediately start using the same.

### Other Capabilities
Though the main focus of this project is to demonstrate how to create MCP tools for REST APIs, but it can also be used to manage **Resources** and **Prompts** as well.  
A **Resource** [Airport_IATA_Code_Lookup.md](rest-api-mcp-server/src/main/resources/Airport_IATA_Code_Lookup.md) is used by AI agents to look up the IATA codes of airports for given origin and destination location names as follows.  

| Location name                                     | City        | Country  | Airport IATA Code  |
|---------------------------------------------------|-------------|----------|--------------------|
| John F. Kennedy International Airport             | New York    | USA      | JFK                |
| Los Angeles International Airport                 | Los Angeles | USA      | LAX                |
| Indira Gandhi International Airport               | Delhi       | India    | DEL                |
| Chhatrapati Shivaji Maharaj International Airport | Mumbai      | India    | BOM                |

> [!NOTE]
> MCP server can be used by any MCP implementing AI client irrespective of language of implementation such as Java, Python and Type Script also.

### Testing MCP server with Claude Desktop
You can test the MCP server using [Claude Desktop](https://claude.ai/download) by following these steps, for details refer to [MCP docs](https://modelcontextprotocol.io/quickstart/server#testing-your-server-with-claude-for-desktop).
* Go to **Claude > Settings > Developer > Edit Config**. It will open `claude_desktop_config.json` configuration file. On macOS, it is located at `~/Library/Application Support/Claude/claude_desktop_config.json`.
* Add the following configuration to the `claude_desktop_config.json` file:
```json
{
  "mcpServers": {
    "rest-api-mcp-server": {
      "command": "~/.sdkman/candidates/java/21.0.6-tem/bin/java",
      "args": [
        "-jar",
        "<Your workspace>/spring-ai-rest-api-mcp-tools-demo/rest-api-mcp-server/target/rest-api-mcp-server-0.0.1.jar"
      ]
    }
  }
}
```
> [!IMPORTANT]
> Set Java 21 path /bin/java in `command` and replace `<Your workspace>` with the actual path to your workspace where the MCP server project is located.
* Restart Claude Desktop and Go to **Claude > Settings > Developer** to check if the MCP server is running. 
  You will be able to see `rest-api-mcp-server` and its status such as `running` or `failed`. 
  You can find the logs by clicking on `Open Logs Folder`, which will open the logs folder (In Mac `~/Library/Application Support/Claude/claude_desktop.log`) and see logs.
* If its successfully started, then you can see MCP Server in effect. Start conversation with Claude Desktop as elaborated in [Conversation with AI Chat Client](#Conversation-with-AI-Chat-Client) section.
* Before your MCP tool usage, it will ask your permission.

> [!CAUTION]
> Though above-mentioned configurations are correct, but it did not work for me, may be because of evolving MCP spec changes. 
> I got following error while trying to run the MCP server from Claude Desktop.
```text
2025-06-01T16:11:54.816Z [info] [rest-api-mcp-server] Message from client: {"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"claude-ai","version":"0.1.0"}},"jsonrpc":"2.0","id":0}
2025-06-01T16:11:55.327Z [error] [rest-api-mcp-server] Unexpected non-whitespace character after JSON at position 4 (line 1 column 5)
```

## Chat client to interact with AI Model and MCP server
Spring boot project [**airline-chat-bot**](airline-chat-bot) implements the AI Chat client for communicating with an AI Model and MCP Server to assist Mock Airline using natural language.
It is implemented using [Spring AI](https://spring.io/projects/spring-ai) [Chat Client API](https://docs.spring.io/spring-ai/reference/api/chatclient.html).

Run [**AirlineChatClientApplication**](airline-chat-bot/src/main/java/ai/ksoot/mcp/airline/client/AirlineChatClientApplication.java) as Spring boot application
* Make sure [**MockAirlineApplication**](mock-airline-service/src/main/java/com/ksoot/airline/MockAirlineApplication.java) is running on configured port.
* Make sure [**RestApiMCPServer**](rest-api-mcp-server/src/main/java/ai/ksoot/rest/mcp/server/RestApiMCPServer.java) is running on configured port.
* Pass the `OPENAI_API_KEY` or `ANTHROPIC_API_KEY` as VM option to use OpenAI or Anthropic AI model respectively. e.g. `-DOPENAI_API_KEY=your_openai_api_key` or `-DANTHROPIC_API_KEY=your_anthropic_api_key`
* Access Swagger UI at [http://localhost:8091/swagger-ui/index.html](http://localhost:8091/swagger-ui/index.html) to explore the available REST API to chat with the client.
* The logs for application do not show up in the console, so you can check the logs in [airline-chat-bot.log](logs/airline-chat-bot.log) file.

> [!CAUTION]
> This Chat client demo does not pick the real-time MCP tool changes from the MCP server. 
> So create the API tools in MCP server before starting the chat client.

### Configurations
Make sure `rest-api-mcp-server` MCP server is configured with its `url` and `toolcallback.enabled` is set to `true` as given below. 
```yaml
spring:
  ai:
    mcp:
      client:
        name: ${spring.application.name}
        version: 0.0.1
        toolcallback.enabled: true
        sse.connections:
          rest-api-mcp-server:
            url: http://localhost:8090
        request-timeout: 5m
```
Chat client can use any AI model that supports MCP tools, such as OpenAI or Anthropic.

#### Using OpenAI model
* Make sure following maven dependencies are added in `pom.xml` file.
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```
* Put configuration in `application.yml` file as follows.
```yaml
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
```
* Pass VM Option `-DOPENAI_API_KEY=your_openai_api_key` in Run configurations to use OpenAI model.

#### Using Anthropic AI model
* Make sure following maven dependencies are added in `pom.xml` file.
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-anthropic</artifactId>
</dependency>
```
* Put configuration in `application.yml` file as follows.
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
```
* Pass VM Option `-DANTHROPIC_API_KEY=your_anthropic_api_key` in Run configurations to use OpenAI model.

### System Prompt
A System Prompt is a hidden instruction given to an AI model at the beginning of a conversation to shape its behavior, tone, knowledge scope, and response style. 
Unlike user input, the system prompt is not visible during the chat but plays a crucial role in guiding the AI’s personality and capabilities.
The AI chat client workflow is controlled by a [**System Prompt**](airline-chat-bot/src/main/java/ai/ksoot/mcp/airline/client/Prompts.java) that defines the role of the AI assistant and its capabilities.  
The system prompt has direct relation with the MCP tools created for the Mock Airline REST service. 
It contais the instructions for the AI model to use the MCP tools, Resources to accomplish given tasks.

### Conversation with AI Chat Client
You can use following API to chat with Mock Airline chat assistant.

```http
GET http://localhost:8090/v1/airline/chat
```
Example Request Curl:
```bash
curl -X 'POST' \
  'http://localhost:8091/v1/airline/chat' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "prompt": "Hi"
}'
```

**Following is the example conversation with AI Chat client**.  
Make the chat API call with the below prompts in sequence as given in **_User_** section. 
Or use Postman Collection [airline-chat-bot.postman_collection.json](Mock%20Airline%20Chat%20assitant.postman_collection.json) and make API calls in sequence.
> [!NOTE]
> Change departure date to tomorrow's date in search flight request and replace PNR with your booking PNR in whichever requests applicable.
---------------------------------------------------------------------------------------
**_User_**: Hi  
**_Assistant_**: Hello! How can I assist you today? Are you looking to search for flights or retrieve booking information?   

**_User_**: I want to travel from Delhi to New York. I want to depart on 2025-06-05, 1 adult and one child  
**_Assistant_**: I found a flight for you from Delhi to New York on June 5, 2025. Here are the details:  
**Flight Details**:
- **Flight Number:** 6E99
- **Airline:** IndiGo
- **Departure:** June 5, 2025, at 14:06 (Delhi - Indira Gandhi International Airport)
- **Arrival:** June 6, 2025, at 14:06 (New York - John F. Kennedy International Airport)
- **Price:** ₹550.75 (INR)
- **Luggage:** First bag up to 20Kg included

Would you like to proceed with booking this flight? If so, please provide the following passenger details:
1. **Adult's First Name**
2. **Adult's Last Name**
3. **Child's First Name**
4. **Child's Last Name**  

**_User_**: Yes, please book this flight for me. My name is Rajveer Singh and my son is Jordan Singh.  
**_Assistant_**: Your flight has been successfully booked! Here are the details:

**Booking Confirmation**
- **PNR:** V2D8Y
- **Flight Number:** 6E986
- **Airline:** IndiGo
- **Departure Airport:** Indira Gandhi International Airport (DEL), Delhi, India
- **Arrival Airport:** John F. Kennedy International Airport (JFK), New York, USA
- **Departure Date:** June 5, 2025, at 21:32
- **Arrival Date:** June 6, 2025, at 21:32
- **Passengers:**
  - **Rajveer Singh (Adult)**
  - **Juno Singh (Child)**
- **Price:** ₹550.75 (INR)
- **Services Included:**
  - 1st Bag (up to 20 Kg)
  - Meal (Snack)

If you need any further assistance or wish to add more services, feel free to ask! Safe travels!

**_User_**: I want to add an extra bag special service.  
**_Assistant_**: The additional bag service has been successfully added to your booking!

**Updated Booking Details**:
- **PNR:** V2D8Y
- **Flight Number:** 6E986
- **Airline:** IndiGo
- **Departure Airport:** Indira Gandhi International Airport (DEL), Delhi, India
- **Arrival Airport:** John F. Kennedy International Airport (JFK), New York, USA
- **Departure Date:** June 5, 2025, at 21:32
- **Passengers:**
  - **Rajveer Singh (Adult)**
  - **Juno Singh (Child)**
- **Current Services:**
  - 1st Bag (up to 20 Kg)
  - Meal (Snack)
  - **Additional Bag Service** (added)

If you need any more assistance or wish to add any other services, feel free to let me know! Safe travels!

**_User_**: I want to see my flight booking details. My PNR is V2D8Y.  
**_Assistant_**: Here are your flight booking details for PNR **V2D8Y**:

**Booking Information**
- **Flight Number:** 6E986
- **Airline:** IndiGo
- **Departure Airport:** Indira Gandhi International Airport (DEL), Delhi, India
- **Arrival Airport:** John F. Kennedy International Airport (JFK), New York, USA
- **Departure Date:** June 5, 2025, at 21:32
- **Arrival Date:** June 6, 2025, at 21:32
- **Passenger Details:**
  - **Rajveer Singh (Adult)**
  - **Juno Singh (Child)**
- **Current Services:**
  - 1st Bag (up to 20 Kg)
  - Meal (Snack)
  - **Additional Bag Service** (2nd bag up to 20 Kg)
- **Booking Status:** Confirmed
- **Total Price:** ₹550.75 (INR)

If you would like to add any more services, make modifications, or need further assistance, please let me know!

**_User_**: I want to cancel my booking.  
**_Assistant_**: Here are the booking details for your flight with PNR **V2D8Y**:

**Booking Information**
- **Flight Number:** 6E986
- **Airline:** IndiGo
- **Departure Airport:** Indira Gandhi International Airport (DEL), Delhi, India
- **Arrival Airport:** John F. Kennedy International Airport (JFK), New York, USA
- **Departure Date:** June 5, 2025, at 21:32
- **Passengers:**
  - **Rajveer Singh (Adult)**
  - **Juno Singh (Child)**
- **Current Services:**
  - 1st Bag (up to 20 Kg)
  - Meal (Snack)
  - Additional Bag Service (2nd bag up to 20 Kg)
- **Booking Status:** Confirmed
- **Total Price:** ₹550.75 (INR)

Would you like to proceed with the cancellation of this booking? Please confirm, and I will process the cancellation for you.

**_User_**: Yes, I confirm. Proceed with cancellation.  
**_Assistant_**: Your booking with PNR **V2D8Y** has been successfully cancelled.

If you need any further assistance or wish to make a new booking, please let me know!

---------------------------------------------------------------------------------------

## Further enhancements
It should be enhanced with the following additional features to make it production grade and more useful.
* Manage Prompts to assist AI agents with pre-defined templates.
* Manage Resources to provide file-like data that can be used in Context by AI agents.
* Implement API to create API tools from a tool definition JSON file.
* The target APIs can be secured with some scheme. So implementation needs to be done to handle the security aspects of the target APIs.
* APIs can be arranged in groups to manage them better. An API group may contain base URL, security aspects, `ToolCallResultConverter`, `ToolExecutionExceptionProcessor`, and other common properties which are currently duplicated across API Tools. 
  That would also help to change such common attributes in one place rather than changing them in all API tools.

## Licence
Open source [**The MIT License**](http://www.opensource.org/licenses/mit-license.php)

## Authors and acknowledgment
[**Rajveer Singh**](https://www.linkedin.com/in/rajveer-singh-589b3950/), In case you find any issues or need any support, please email me at raj14.1984@gmail.com.
Please give me a :star: on Github and a :clap: on [**medium.com**](https://officiallysingh.medium.com/turning-rest-apis-to-ai-callable-mcp-tools-372df8b0b84a) if you find it helpful.

## References
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [MCP Server Boot Starter](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html)
- [MCP Client Boot Starter](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-client-docs.html)
- [Model Context Protocol](https://modelcontextprotocol.io/introduction)
- [Model Context Protocol Specification](https://github.com/modelcontextprotocol/modelcontextprotocol)
- [Spring Boot Auto-configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration)
- [JSON Schema guide](https://json-schema.org/learn)
- [A Visual guide to LLM agents](https://newsletter.maartengrootendorst.com/p/a-visual-guide-to-llm-agents) by [Maarten Grootendorst](https://substack.com/@maartengrootendorst)
- [A visual guide to reasoning LLMs](https://newsletter.maartengrootendorst.com/p/a-visual-guide-to-reasoning-llms) by [Maarten Grootendorst](https://substack.com/@maartengrootendorst)
