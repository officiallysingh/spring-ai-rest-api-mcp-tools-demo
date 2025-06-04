package ai.ksoot.mcp.airline.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/airline")
@Tag(name = "Airline Chat", description = "APIs.")
@Slf4j
@RequiredArgsConstructor
public class AirlineChatController {

  private final ChatClient chatClient;

  //  @Operation(
  //      operationId = "get-weather-forecast",
  //      summary = "Get weather forecast for the given city.")
  //  @GetMapping("/weather/{city}/forecast")
  //  String weatherForecast(
  //      @Parameter(description = "City name", example = "Sacramento", required = true)
  // @PathVariable
  //          final String city) {
  //    PromptTemplate pt =
  //        new PromptTemplate(
  //            """
  //            What’s the weather in {city}?.
  //            """);
  //    Prompt p = pt.create(Map.of("city", city));
  //    return this.chatClient.prompt(p).call().content();
  //  }
  //
  //  @Operation(operationId = "get-weather-alerts", summary = "Get weather alerts for the given
  // city.")
  //  @GetMapping("/weather/{city}/alerts")
  //  String weatherAlerts(
  //      @Parameter(description = "City name", example = "Texas", required = true) @PathVariable
  //          final String city) {
  //
  //    PromptTemplate pt =
  //        new PromptTemplate(
  //            """
  //            What are the active weather alerts in {city}?.
  //            """);
  //    Prompt p = pt.create(Map.of("city", city));
  //    return this.chatClient.prompt(p).call().content();
  //  }
  //
  //  @Operation(operationId = "search-flights", summary = "Search flights.")
  //  @GetMapping("/flights")
  //  String searchFlights() {
  //
  //    String origin = "DEL";
  //    String destination = "JFK";
  //    String departureDate = "2025-04-26";
  //
  //    PromptTemplate pt =
  //        new PromptTemplate(
  //            """
  //            Search flights from {origin} to {destination} for date {departureDate}?.
  //            """);
  //    Prompt p =
  //        pt.create(
  //            Map.of("origin", origin, "destination", destination, "departureDate",
  // departureDate));
  //    return this.chatClient.prompt(p).call().content();
  //  }

  @Operation(operationId = "chat", summary = "Chat with Airline assistant.")
  @PostMapping("/chat")
  String chat(@RequestBody @Valid final ChatRequest chatRequest) {
    log.info("Chat request: {}", chatRequest);
    final String response = this.chatClient.prompt(chatRequest.prompt).call().content();
    log.info("Chat response: {}", response);
    return response;
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  static class ChatRequest {

    @NotEmpty private String prompt;

    //    private Map<String, String> variables;

  }
}
