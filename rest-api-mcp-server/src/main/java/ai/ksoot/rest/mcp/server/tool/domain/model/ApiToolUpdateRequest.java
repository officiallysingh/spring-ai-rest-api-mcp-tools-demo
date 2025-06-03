package ai.ksoot.rest.mcp.server.tool.domain.model;

import static ai.ksoot.rest.mcp.server.tool.domain.model.ApiSchema.SEARCH_FLIGHTS_SCHEMA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@ToString
@Valid
@Schema(description = "Update Api Tool request")
public class ApiToolUpdateRequest {

  @Schema(
      description = "Unique Tool name.",
      example = "search_flights",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(
      description = "Tool description.",
      example = "Search available flights as per origin, destination and departure date",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String description;

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
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Boolean returnDirect;

  @Schema(
      description = "Target REST API base URL.",
      example = "http://localhost:8080",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String baseUrl;

  @Schema(
      description = "Target API Http Method.",
      allowableValues = {"GET", "POST", "PUT", "PATCH", "DELETE"},
      example = "POST",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private HttpMethod httpMethod;

  @Schema(
      description = "Default Http Headers.",
      example = "{\"Accept\": [\"application/json\"], \"Content-Type\": [\"application/json\"]}",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private HttpHeaders defaultHeaders;

  @Schema(
      description = "Runtime Http Header names.",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<String> headers;

  @Schema(
      description =
          "Target REST API path. It should be relative to the base URL and may contain Path variables.",
      example = "/v1/flight/{pnr}",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String path;

  @Schema(
      description = "Names of the request query parameters.",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<String> queryParams = new ArrayList<>();

  @Schema(
      description = "Name of the request body argument.",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String bodyArg;

  @JsonIgnore
  public boolean isEmpty() {
    return StringUtils.isBlank(this.name)
        && StringUtils.isBlank(this.description)
        && StringUtils.isBlank(this.inputSchema)
        && Objects.isNull(this.returnDirect)
        && StringUtils.isBlank(this.baseUrl)
        && Objects.isNull(this.httpMethod)
        && Objects.isNull(this.defaultHeaders)
        && CollectionUtils.isEmpty(this.headers)
        && StringUtils.isBlank(this.path)
        && CollectionUtils.isEmpty(this.queryParams)
        && StringUtils.isBlank(this.bodyArg);
  }
}
