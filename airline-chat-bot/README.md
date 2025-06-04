## Running Airline Chat Client
Run [**AirlineChatClientApplication**](src/main/java/ai/ksoot/mcp/airline/client/AirlineChatClientApplication.java) as Spring boot application
* Make sure [**MockAirlineApplication**](../mock-airline-service/src/main/java/com/ksoot/airline/MockAirlineApplication.java) is running on configured port.
* Make sure [**RestApiMCPServer**](../rest-api-mcp-server/src/main/java/ai/ksoot/rest/mcp/server/RestApiMCPServer.java) is running on configured port.
* Pass the `OPENAI_API_KEY` or `ANTHROPIC_API_KEY` as VM option to use OpenAI or Anthropic AI model respectively. e.g. `-DOPENAI_API_KEY=your_openai_api_key` or `-DANTHROPIC_API_KEY=your_anthropic_api_key`
* Access Swagger UI at [http://localhost:8091/swagger-ui/index.html](http://localhost:8091/swagger-ui/index.html) to explore the available REST API to chat with the client.
* The logs for application do not show up in the console, so you can check the logs in [airline-chat-bot.log](../logs/airline-chat-bot.log) file.

### Configurations
`rest-api-mcp-server` MCP server is configured with its `url` below.
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

