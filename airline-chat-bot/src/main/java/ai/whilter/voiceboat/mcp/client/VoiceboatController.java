package ai.whilter.voiceboat.mcp.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/voiceboat")
@Tag(name = "Voiceboat", description = "APIs.")
@Slf4j
@RequiredArgsConstructor
public class VoiceboatController {

  private final ChatClient chatClient;

  //  private final List<McpSyncClient> mcpSyncClients;

  //  public VoiceboatController(
  //      final ChatClient.Builder chatClientBuilder,
  ////      final ToolCallbackProvider tools,
  //      final List<McpSyncClient> mcpSyncClients) {
  //    this.chatClient =
  //        chatClientBuilder
  //            .defaultSystem(Prompts.SYSTEM)
  ////            .defaultTools(tools)
  //            .defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClients))
  //            .defaultAdvisors(
  //                new PromptChatMemoryAdvisor(new InMemoryChatMemory()), new
  // SimpleLoggerAdvisor())
  //            .build();
  ////    this.chatClient.mutate().
  //    this.mcpSyncClients = mcpSyncClients;
  //  }

  //    @GetMapping("/search-flight")
  //    String findByNationality(@PathVariable String nationality) {
  //
  //        PromptTemplate pt = new PromptTemplate("""
  //                Find persons with {nationality} nationality.
  //                """);
  //        Prompt p = pt.create(Map.of("nationality", nationality));
  //        return this.chatClient.prompt(p)
  //                .call()
  //                .content();
  //    }

  @Operation(
      operationId = "get-weather-forecast",
      summary = "Get weather forecast for the given city.")
  @GetMapping("/weather/{city}/forecast")
  String weatherForecast(
      @Parameter(description = "City name", example = "Sacramento", required = true) @PathVariable
          final String city) {
    PromptTemplate pt =
        new PromptTemplate(
            """
            What’s the weather in {city}?.
            """);
    Prompt p = pt.create(Map.of("city", city));
    return this.chatClient.prompt(p).call().content();
  }

  @Operation(operationId = "get-weather-alerts", summary = "Get weather alerts for the given city.")
  @GetMapping("/weather/{city}/alerts")
  String weatherAlerts(
      @Parameter(description = "City name", example = "Texas", required = true) @PathVariable
          final String city) {

    PromptTemplate pt =
        new PromptTemplate(
            """
            What are the active weather alerts in {city}?.
            """);
    Prompt p = pt.create(Map.of("city", city));
    return this.chatClient.prompt(p).call().content();
  }

  @Operation(operationId = "search-flights", summary = "Search flights.")
  @GetMapping("/flights")
  String searchFlights() {

    String origin = "DEL";
    String destination = "JFK";
    String departureDate = "2025-04-26";

    PromptTemplate pt =
        new PromptTemplate(
            """
            Search flights from {origin} to {destination} for date {departureDate}?.
            """);
    Prompt p =
        pt.create(
            Map.of("origin", origin, "destination", destination, "departureDate", departureDate));
    return this.chatClient.prompt(p).call().content();
  }

  @Operation(operationId = "chat", summary = "Chat with Airline assistant.")
  @GetMapping("/chat")
  String chat(final ChatRequest chatRequest) {
    log.info("Chat request: {}", chatRequest);
    String response = this.chatClient.prompt(chatRequest.prompt).call().content();
    log.info("Chat response: {}", response);
    return response;
  }

  //    Prompt loadPromptByName(String name, String nationality) {
  //        McpSchema.GetPromptRequest r = new McpSchema.GetPromptRequest(name,
  // Map.of("nationality", nationality));
  //        var client = mcpSyncClients.stream()
  //                .filter(c -> c.getServerInfo().name().equals("person-mcp-server"))
  //                .findFirst();
  //        if (client.isPresent()) {
  //            var content = (McpSchema.TextContent)
  // client.get().getPrompt(r).messages().getFirst().content();
  //            PromptTemplate pt = new PromptTemplate(content.text());
  //            Prompt p = pt.create(Map.of("nationality", nationality));
  //            log.info("Prompt: {}", p);
  //            return p;
  //        } else return null;
  //    }

  @Data
  public class ChatRequest {

    private String prompt;

    //    private Map<String, String> variables;

  }
}
