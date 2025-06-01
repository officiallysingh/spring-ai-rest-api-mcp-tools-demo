package ai.whilter.voiceboat.mcp.server.tool.domain;

import ai.whilter.voiceboat.mcp.server.tool.domain.model.ApiToolCallbackProvider;
import ai.whilter.voiceboat.mcp.server.tool.domain.service.ApiToolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;

@Slf4j
@Configuration
public class McpServerConfig {

  private static final String MOCK_AIRLINE_BASE_URL = "http://localhost:8092";

  private final Resource airportsLookup;

  private final ApiToolService apiToolService;

  McpServerConfig(
      @Value("classpath:Airport_IATA_Code_Lookup.md") final Resource airportsLookup,
      final ApiToolService apiToolService) {
    this.airportsLookup = airportsLookup;
    this.apiToolService = apiToolService;
  }

  @Bean
  public ApiToolCallbackProvider apiToolCallbackProvider() {
    return ApiToolCallbackProvider.of(this.apiToolService.getAllApiTools());
  }

  public record TextInput(String input) {}

  @Bean
  public ToolCallback dummy() {
    return FunctionToolCallback.builder("dummy", (TextInput input) -> input.input().toUpperCase())
        .inputType(TextInput.class)
        .description("Just a dummy tool, should never be called")
        .build();
  }

  //  @Transactional
  //  @Bean
  //  public ToolCallbackProvider airlineTools() {
  //    final ApiToolCallback searchFlightsTool =
  //        ApiToolCallback.tool(
  //                "search_flights",
  //                "Search available flights as per origin, destination and departure date",
  //                ApiSchema.SEARCH_FLIGHTS_SCHEMA)
  //            .baseUrl(MOCK_AIRLINE_BASE_URL)
  //            .path("/v1/airline/flights")
  //            .postMethod()
  //            .bodyArg("airShopRequest")
  //            .build();
  //
  //    final ApiToolCallback bookFlightTool =
  //        ApiToolCallback.tool(
  //                "book_flight",
  //                "Book a flight as per given flight number and passenger details",
  //                ApiSchema.BOOK_FLIGHT_SCHEMA)
  //            .baseUrl(MOCK_AIRLINE_BASE_URL)
  //            .path("/v1/airline/bookings")
  //            .postMethod()
  //            .bodyArg("bookingRequest")
  //            .build();
  //
  //    final ApiToolCallback getBookingTool =
  //        ApiToolCallback.tool(
  //                "get_flight_booking_by_pnr",
  //                "Get flight booking details by PNR",
  //                ApiSchema.GET_BOOKING_SCHEMA)
  //            .baseUrl(MOCK_AIRLINE_BASE_URL)
  //            .path("/v1/airline/bookings/{pnr}")
  //            .getMethod()
  //            .build();
  //
  //    final ApiToolCallback cancelBookingTool =
  //        ApiToolCallback.tool(
  //                "cancel_booking_by_pnr",
  //                "Cancel a flight booking by PNR",
  //                ApiSchema.CANCEL_BOOKING_SCHEMA)
  //            .baseUrl(MOCK_AIRLINE_BASE_URL)
  //            .path("/v1/airline/bookings/{pnr}/cancel")
  //            .putMethod()
  //            .build();
  //
  //    ApiToolCallbackProvider apiToolCallbackProvider =
  //        ApiToolCallbackProvider.of(
  //            searchFlightsTool, bookFlightTool, getBookingTool, cancelBookingTool);
  //
  ////    this.saveToolsIfNotExists(apiToolCallbackProvider);
  //
  //    return apiToolCallbackProvider;
  //  }
  //
  //  private void saveToolsIfNotExists(final ApiToolCallbackProvider apiToolCallbackProvider) {
  //    Arrays.stream(apiToolCallbackProvider.getToolCallbacks())
  //        .filter(ApiToolCallback.class::isInstance)
  //        .map(ApiToolCallback.class::cast)
  //        .forEach(
  //            tool -> {
  //              if (this.apiToolCallbackRepository.existsByToolDefinitionName(
  //                  tool.getToolDefinition().name())) {
  //                log.info(
  //                    "Tool with name {} already exists, skipping creation",
  //                    tool.getToolDefinition().name());
  //              } else {
  //                log.info(
  //                    "Tool with name {} does not exist, creating it",
  //                    tool.getToolDefinition().name());
  //                this.apiToolCallbackRepository.save(tool);
  //              }
  //            });
  //  }

  @Bean
  public List<McpServerFeatures.SyncResourceSpecification> myResources() throws IOException {
    final URI uri = this.airportsLookup.getURI();
    log.debug("Airports Lookup Resource uri: {}", uri);
    var airportCodesLookupResource =
        new McpSchema.Resource(
            uri.toString(),
            "airport_iata_code_lookup",
            "Lookup Airport IATA Codes by city, country or Location names",
            MimeTypeUtils.TEXT_PLAIN_VALUE,
            null);
    var resourceSpecification =
        new McpServerFeatures.SyncResourceSpecification(
            airportCodesLookupResource,
            (exchange, request) -> {
              try {
                String jsonContent =
                    new ObjectMapper().writeValueAsString(airportCodesLookupResource);
                return new McpSchema.ReadResourceResult(
                    List.of(
                        new McpSchema.TextResourceContents(
                            request.uri(), MimeTypeUtils.APPLICATION_JSON_VALUE, jsonContent)));
              } catch (Exception e) {
                throw new RuntimeException("Failed to generate system info", e);
              }
            });

    return List.of(resourceSpecification);
  }
}
