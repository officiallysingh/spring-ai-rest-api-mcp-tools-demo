package ai.whilter.voiceboat.mcp.server.tool.domain.model;

import static ai.whilter.voiceboat.mcp.server.tool.domain.model.ApiSchema.SEARCH_FLIGHTS_SCHEMA;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@ToString
@Schema(description = "Api Tool response")
public class ApiToolResponse {

  @Schema(
      description = "API Tool Id.",
      example = "6814699dd0feee791b90c8d1",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String id;

  @NotEmpty
  @Schema(
      description = "Unique Tool name.",
      example = "search_flights",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotEmpty
  @Schema(
      description = "Tool description.",
      example = "Get flight details",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String description;

  @NotEmpty
  @Schema(
      description = "Tool input JSON Schema.",
      example = SEARCH_FLIGHTS_SCHEMA,
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String inputSchema;

  @Schema(
      description =
          "Whether the tool result should be returned directly or passed back to the model.",
      defaultValue = "false",
      example = "false",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private boolean returnDirect;

  @NotEmpty
  @Schema(
      description = "Target REST API base URL.",
      example = "http://localhost:8080",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String baseUrl;

  @NotNull
  @Schema(
      description = "Tool input JSON Schema.",
      example = "POST",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private HttpMethod httpMethod;

  @Schema(
      description = "Default Http Headers.",
      example =
          """
          {
              "Accept": "application/json",
              "Content-Type": "application/json",
          }
          """,
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private HttpHeaders defaultHeaders;

  @Schema(
      description = "Runtime Http Header names.",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<String> headers;

  @NotEmpty
  @Schema(
      description =
          "Target REST API path. It should be relative to the base URL and may contain Path variables.",
      example = "/v1/flight/{pnr}",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String path;

  @Schema(
      description = "Names of the request query parameters.",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<String> queryParams = new ArrayList<>();

  @Schema(
      description = "Name of the request body argument.",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String bodyArg;
}
