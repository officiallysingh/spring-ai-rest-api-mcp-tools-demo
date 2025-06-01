package ai.whilter.voiceboat.mcp.server.tool.domain.model;

import static ai.whilter.voiceboat.mcp.server.common.mongo.MongoSchema.COLLECTION_TOOLS;

import ai.whilter.voiceboat.mcp.server.common.mongo.AbstractEntity;
import ai.whilter.voiceboat.mcp.server.common.mongo.Auditable;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.execution.DefaultToolCallResultConverter;
import org.springframework.ai.tool.execution.ToolCallResultConverter;
import org.springframework.ai.tool.metadata.DefaultToolMetadata;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Getter
@Auditable
@Document(collection = COLLECTION_TOOLS)
@TypeAlias("api_tool")
@Slf4j
public class ApiToolCallback extends AbstractEntity implements ToolCallback {

  private static final HttpHeaders DEFAULT_HEADERS = new HttpHeaders();

  static {
    DEFAULT_HEADERS.setAccept(List.of(MediaType.ALL));
    DEFAULT_HEADERS.setContentType(MediaType.APPLICATION_JSON);
  }

  private static final ToolCallResultConverter DEFAULT_RESULT_CONVERTER =
      new DefaultToolCallResultConverter();
  private static final DefaultToolMetadata DEFAULT_TOOL_METADATA =
      (DefaultToolMetadata) DefaultToolMetadata.builder().build();
  private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{([^/}]+)}");

  @Field(name = "tool_definition")
  private DefaultToolDefinition toolDefinition;

  @Field(name = "return_direct")
  private DefaultToolMetadata toolMetadata;

  @Field(name = "http_method")
  private HttpMethod httpMethod;

  @Field(name = "default_headers")
  private HttpHeaders defaultHeaders;

  @Field(name = "base_url")
  private String baseUrl;

  @Field(name = "path")
  private String path;

  @Field(name = "path_variables")
  private List<String> pathVariables;

  @Field(name = "query_params")
  private List<String> queryParams;

  @Field(name = "headers")
  private List<String> headers;

  @Field(name = "body_arg")
  private String bodyArg;

  @Transient private final RestClient restClient;

  @Transient
  private final ToolCallResultConverter toolCallResultConverter = DEFAULT_RESULT_CONVERTER;

  @PersistenceCreator
  protected ApiToolCallback(
      final DefaultToolDefinition toolDefinition,
      @Nullable final DefaultToolMetadata toolMetadata,
      //      @Nullable final ToolCallResultConverter toolCallResultConverter,
      final String baseUrl,
      final HttpMethod httpMethod,
      final HttpHeaders defaultHeaders,
      final String path,
      @Nullable final List<String> queryParams,
      @Nullable final List<String> headers,
      @Nullable final String bodyArg) {
    Assert.notNull(toolDefinition, "'toolDefinition' is required");
    Assert.notNull(httpMethod, "'httpMethod' is required");
    Assert.hasText(baseUrl, "'baseUrl' is required");
    Assert.hasText(path, "'path' is required");
    Assert.noNullElements(queryParams, "'queryParams' cannot contain null elements");
    Assert.noNullElements(headers, "'headers' cannot contain null elements");
    if (httpMethod == HttpMethod.GET) {
      Assert.state(StringUtils.isBlank(bodyArg), "'bodyArg' not allowed for HttpMethod.GET");
    }

    this.toolDefinition = toolDefinition;
    this.toolMetadata = toolMetadata != null ? toolMetadata : DEFAULT_TOOL_METADATA;
    this.baseUrl = baseUrl;
    this.httpMethod = httpMethod;
    this.defaultHeaders = MapUtils.isEmpty(defaultHeaders) ? DEFAULT_HEADERS : defaultHeaders;
    this.path = path;
    this.pathVariables = extractPathVariables(path);
    this.queryParams = queryParams;
    this.headers = headers;
    this.bodyArg = bodyArg;
    this.restClient =
        RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeaders(httpHeaders -> httpHeaders.addAll(DEFAULT_HEADERS))
            .build();

    //    this.toolCallResultConverter =
    //        toolCallResultConverter != null ? toolCallResultConverter : DEFAULT_RESULT_CONVERTER;
  }

  private List<String> extractPathVariables(final String path) {
    final List<String> pathVars = new ArrayList<>();
    final Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
    while (matcher.find()) {
      pathVars.add(matcher.group(1));
    }
    return pathVars;
  }

  @Override
  public DefaultToolDefinition getToolDefinition() {
    return this.toolDefinition;
  }

  @Override
  public DefaultToolMetadata getToolMetadata() {
    return this.toolMetadata;
  }

  @Override
  public String call(final String toolInput) {
    log.debug("Tool call request received for input json: {}", toolInput);
    return call(toolInput, null);
  }

  @Override
  public String call(final String toolInput, @Nullable final ToolContext toolContext) {
    Assert.hasText(toolInput, "toolInput cannot be null or empty");

    log.debug("Starting execution of tool: {}", toolDefinition.name());

    final Map<String, Object> toolArgs = this.extractToolArguments(toolInput);

    final Map<String, Object> pathVars = new HashMap<>();
    if (CollectionUtils.isNotEmpty(this.pathVariables)) {
      this.pathVariables.forEach(
          pathVar -> {
            if (toolArgs.containsKey(pathVar)) {
              Object value = toolArgs.get(pathVar);
              pathVars.put(pathVar, value);
            }
          });
    }

    final MultiValueMap<String, String> queryVars = new LinkedMultiValueMap<>();
    if (CollectionUtils.isNotEmpty(this.queryParams)) {
      this.queryParams.forEach(
          queryVar -> {
            if (toolArgs.containsKey(queryVar)) {
              Object value = toolArgs.get(queryVar);
              if (Objects.nonNull(value)) {
                queryVars.put(queryVar, List.of(value.toString()));
              }
            }
          });
    }

    final MultiValueMap<String, String> headerVars = new LinkedMultiValueMap<>();
    if (CollectionUtils.isNotEmpty(this.headers)) {
      this.headers.forEach(
          headerVar -> {
            if (toolArgs.containsKey(headerVar)) {
              Object value = toolArgs.get(headerVar);
              if (Objects.nonNull(value)) {
                headerVars.put(headerVar, List.of(value.toString()));
              }
            }
          });
    }

    final String body = JsonParser.toJson(toolArgs.get(this.bodyArg));

    final Function<UriBuilder, URI> uriFunction =
        uriBuilder -> uriBuilder.path(this.path).queryParams(queryVars).build(pathVars);

    final HttpHeaders finalHeaders = new HttpHeaders();
    if (MapUtils.isNotEmpty(this.defaultHeaders)) {
      finalHeaders.putAll(this.defaultHeaders);
    }
    if (MapUtils.isNotEmpty(headerVars)) {
      finalHeaders.putAll(headerVars);
    }

    final String response =
        this.restClient
            .method(this.httpMethod)
            .uri(uriFunction)
            .headers(headers -> headers.addAll(finalHeaders))
            .body(body)
            .retrieve()
            .body(String.class);

    //    final String response =
    //        this.restClient
    //            .method(this.httpMethod)
    //            .uri(this.uri, pathVars)
    //            .body(body)
    //            .retrieve()
    ////            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
    ////              throw new MyCustomRuntimeException(response.getStatusCode(),
    // response.getHeaders());
    ////            })
    //            .body(String.class);

    return this.toolCallResultConverter.convert(response, String.class);
  }

  private Map<String, Object> extractToolArguments(final String toolInput) {
    return JsonParser.fromJson(toolInput, new TypeReference<>() {});
  }

  @Override
  public boolean equals(Object other) {
    if (other == null || getClass() != other.getClass()) return false;
    if (!super.equals(other)) return false;
    ApiToolCallback that = (ApiToolCallback) other;
    return Objects.equals(this.toolDefinition.name(), that.toolDefinition.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.toolDefinition.name());
  }

  @Override
  public String toString() {
    return "ApiToolCallback{"
        + "toolDefinition="
        + this.toolDefinition
        + ", toolMetadata="
        + this.toolMetadata
        + ", baseUrl="
        + this.baseUrl
        + ", httpMethod="
        + this.httpMethod
        + ", path="
        + this.path
        + ", pathVariables="
        + this.pathVariables
        + ", queryParams="
        + this.queryParams
        + ", bodyArg="
        + this.bodyArg
        + '}';
  }

  public static BaseUrlBuilder tool(
      final String name, final String description, final String inputSchema) {
    return new ApiToolCallbackArgBuilder(
        (DefaultToolDefinition)
            DefaultToolDefinition.builder()
                .name(name)
                .description(description)
                .inputSchema(inputSchema)
                .build(),
        false);
  }

  public static BaseUrlBuilder of(final DefaultToolDefinition toolDefinition) {
    return new ApiToolCallbackArgBuilder(toolDefinition, false);
  }

  public static BaseUrlBuilder of(
      final DefaultToolDefinition toolDefinition, final boolean returnDirect) {
    return new ApiToolCallbackArgBuilder(toolDefinition, returnDirect);
  }

  public interface BaseUrlBuilder {

    PathBuilder baseUrl(final String baseUrl);
  }

  public interface PathBuilder {

    HttpMethodBuilder path(final String path);
  }

  public interface HttpMethodBuilder {

    BodyArgBuilder httpMethod(final HttpMethod httpMethod);

    QueryParamBuilder getMethod();

    BodyArgBuilder postMethod();

    BodyArgBuilder putMethod();

    BodyArgBuilder patchMethod();

    BodyArgBuilder deleteMethod();
  }

  public interface BodyArgBuilder extends QueryParamBuilder {

    QueryParamBuilder bodyArg(@Nullable final String bodyArg);
  }

  public interface QueryParamBuilder extends DefaultHeadersBuilder {

    DefaultHeadersBuilder queryParams(@Nullable final List<String> params);

    QueryParamBuilder queryParam(@Nullable final String param);
  }

  public interface DefaultHeadersBuilder extends HeaderBuilder {

    HeaderBuilder defaultHeaders(@Nullable HttpHeaders defaultHeaders);
  }

  public interface HeaderBuilder extends org.apache.commons.lang3.builder.Builder<ApiToolCallback> {

    org.apache.commons.lang3.builder.Builder<ApiToolCallback> headers(
        @Nullable final List<String> headers);

    HeaderBuilder header(@Nullable final String header);
  }

  //  @RequiredArgsConstructor
  public static class ApiToolCallbackArgBuilder
      implements BaseUrlBuilder, PathBuilder, HttpMethodBuilder, BodyArgBuilder {

    private final DefaultToolDefinition toolDefinition;
    private final DefaultToolMetadata toolMetadata;

    private String baseUrl;
    private HttpMethod httpMethod;
    private String path;

    private final List<String> queryParams = new ArrayList<>();
    private final HttpHeaders defaultHeaders = new HttpHeaders();
    private final List<String> headers = new ArrayList<>();
    private String bodyArg;

    public ApiToolCallbackArgBuilder(
        final DefaultToolDefinition toolDefinition, final boolean returnDirect) {
      this.toolDefinition = toolDefinition;
      this.toolMetadata =
          (DefaultToolMetadata) ToolMetadata.builder().returnDirect(returnDirect).build();
    }

    @Override
    public PathBuilder baseUrl(final String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    @Override
    public HttpMethodBuilder path(final String path) {
      this.path = path;
      return this;
    }

    @Override
    public BodyArgBuilder httpMethod(HttpMethod httpMethod) {
      this.httpMethod = httpMethod;
      return this;
    }

    @Override
    public QueryParamBuilder getMethod() {
      this.httpMethod = HttpMethod.GET;
      return this;
    }

    @Override
    public BodyArgBuilder postMethod() {
      this.httpMethod = HttpMethod.POST;
      return this;
    }

    @Override
    public BodyArgBuilder putMethod() {
      this.httpMethod = HttpMethod.PUT;
      return this;
    }

    @Override
    public BodyArgBuilder patchMethod() {
      this.httpMethod = HttpMethod.PATCH;
      return this;
    }

    @Override
    public BodyArgBuilder deleteMethod() {
      this.httpMethod = HttpMethod.DELETE;
      return this;
    }

    @Override
    public QueryParamBuilder bodyArg(final String bodyArg) {
      this.bodyArg = bodyArg;
      return this;
    }

    @Override
    public DefaultHeadersBuilder queryParams(final List<String> params) {
      if (CollectionUtils.isNotEmpty(params)) {
        this.queryParams.addAll(params);
      }
      return this;
    }

    @Override
    public QueryParamBuilder queryParam(final String param) {
      if (StringUtils.isNotBlank(param)) {
        this.queryParams.add(param);
      }
      return this;
    }

    @Override
    public HeaderBuilder defaultHeaders(final HttpHeaders defaultHeaders) {
      if (MapUtils.isNotEmpty(defaultHeaders)) {
        this.defaultHeaders.putAll(defaultHeaders);
      }
      return this;
    }

    @Override
    public org.apache.commons.lang3.builder.Builder<ApiToolCallback> headers(
        final List<String> headers) {
      if (CollectionUtils.isNotEmpty(headers)) {
        this.headers.addAll(headers);
      }
      return this;
    }

    @Override
    public HeaderBuilder header(final String header) {
      if (StringUtils.isNotBlank(header)) {
        this.headers.add(header);
      }
      return this;
    }

    @Override
    public ApiToolCallback build() {
      return new ApiToolCallback(
          this.toolDefinition,
          this.toolMetadata,
          this.baseUrl,
          this.httpMethod,
          this.defaultHeaders,
          this.path,
          this.queryParams,
          this.headers,
          this.bodyArg);
    }
  }
}
