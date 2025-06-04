package ai.ksoot.rest.mcp.server.tool.domain.service;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolCallback;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.SchemaVersion;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OpenApiSpecParser {

  //  private final Resource airlineSpec;
  //
  //  OpenApiSpecParser(@Value("classpath:oas/airline.json") final Resource airlineSpec) {
  //    this.airlineSpec = airlineSpec;
  //  }

  public List<ApiToolCallback> parse(final Resource resource) throws IOException {
    //    final String specUrl = this.airlineSpec.getURI().toString();
    //    final SwaggerParseResult result = new OpenAPIParser().readLocation(specUrl, null, null);

    // Read the content directly from the resource instead of trying to get its URL
    final String content = new String(resource.getInputStream().readAllBytes());

    // Parse the OpenAPI spec directly from the content
    ParseOptions parseOptions = new ParseOptions();
    parseOptions.setResolve(true);

    final SwaggerParseResult result = new OpenAPIParser().readContents(content, null, parseOptions);
    final OpenAPI openAPI = result.getOpenAPI();

    final List<ApiToolCallback> apiToolCallbacks = new ArrayList<>();

    final String baseUrl = openAPI.getServers().getFirst().getUrl();

    final Paths paths = openAPI.getPaths();
    log.debug("--------------------  Parsed API Tool Definitions  --------------------");
    for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
      final String path = entry.getKey();
      final PathItem pathItem = entry.getValue();

      final Map<PathItem.HttpMethod, Operation> httpMethodOperationMap =
          pathItem.readOperationsMap();
      for (Map.Entry<PathItem.HttpMethod, Operation> httpMethodOperationEntry :
          httpMethodOperationMap.entrySet()) {

        final HttpMethod httpMethod = HttpMethod.valueOf(httpMethodOperationEntry.getKey().name());
        final Operation operation = httpMethodOperationEntry.getValue();

        final ApiToolCallback apiToolCallback =
            this.buildApiToolCallback(
                baseUrl, path, httpMethod, operation, openAPI.getComponents());
        log.debug(apiToolCallback.toString());
        apiToolCallbacks.add(apiToolCallback);
      }
    }
    log.debug("---------------------------------------------------------------------");

    //    System.out.println(apiToolCallbacks);

    return apiToolCallbacks;
  }

  private ApiToolCallback buildApiToolCallback(
      final String baseUrl,
      final String path,
      final HttpMethod httpMethod,
      final Operation operation,
      final Components components) {
    final ObjectNode inputSchema = JsonParser.getObjectMapper().createObjectNode();
    inputSchema.put("$schema", SchemaVersion.DRAFT_2020_12.getIdentifier());
    inputSchema.put(Attributes.TYPE, Attributes.OBJECT);
    ObjectNode propertiesNode = inputSchema.putObject(Attributes.PROPERTIES);
    final List<String> requiredProperties = new ArrayList<>();

    final List<MediaType> acceptableMediaTypes = this.getAcceptableMediaTypes(operation);
    MediaType contentType = null;

    final String toolName =
        StringUtils.isBlank(operation.getOperationId())
            ? httpMethod + "_" + path.replaceAll("[{}]", "").replaceAll("/", "_")
            : operation.getOperationId();
    final String opSummary = operation.getSummary();
    final String opDescription = operation.getDescription();

    final String toolDescription =
        StringUtils.isBlank(opSummary)
            ? toolName
                + " operation"
                + (StringUtils.isBlank(opDescription) ? "" : ". " + opDescription)
            : opSummary + (StringUtils.isBlank(opDescription) ? "" : ". " + opDescription);

    String requestBodyArgName = null;
    final List<String> queryParams = new ArrayList<>();
    final List<String> headers = new ArrayList<>();

    final List<Parameter> parameters = operation.getParameters();
    if (CollectionUtils.isNotEmpty(parameters)) {

      for (Parameter parameter : parameters) {
        ParamIn paramIn = ParamIn.valueOf(parameter.getIn());
        if (paramIn == ParamIn.query) {
          queryParams.add(parameter.getName());
        } else if (paramIn == ParamIn.header) {
          headers.add(parameter.getName());
        }
        // Path variables are derived from path in ApiToolCallback constructor

        ObjectNode parameterNode = propertiesNode.putObject(parameter.getName());
        this.putType(parameter.getSchema(), parameterNode, components);
        this.putFormat(parameter.getSchema(), parameterNode);
        this.putDescription(parameter.getSchema(), parameterNode);
        this.putEnum(parameter.getSchema(), parameterNode);
        this.putPattern(parameter.getSchema(), parameterNode);
        this.putExample(parameter.getSchema(), parameterNode);
        final Boolean required = parameter.getRequired();
        if (required != null && required) {
          requiredProperties.add(parameter.getName());
        }
      }
    }

    final RequestBody requestBody = operation.getRequestBody();
    if (Objects.nonNull(requestBody)) {
      contentType = this.getContentType(requestBody);
      requestBodyArgName =
          this.buildRequestBodySchema(propertiesNode, requestBody, contentType, components);
      final Boolean required = requestBody.getRequired();
      if (required != null && required) {
        requiredProperties.add(requestBodyArgName);
      }
    }

    if (CollectionUtils.isNotEmpty(requiredProperties)) {
      var requiredArray = inputSchema.putArray(Attributes.REQUIRED);
      requiredProperties.forEach(requiredArray::add);
    }
    inputSchema.put(Attributes.ADDITIONAL_PROPERTIES, false);

    final String inputSchemaString = inputSchema.toPrettyString();
    //    System.out.println("inputSchema --->> " + inputSchemaString);

    final HttpHeaders defaultHeaders = new HttpHeaders();
    defaultHeaders.setAccept(acceptableMediaTypes);
    if (contentType != null) {
      defaultHeaders.setContentType(contentType);
    }

    ApiToolCallback apiToolCallback =
        ApiToolCallback.tool(toolName, toolDescription, inputSchemaString)
            .baseUrl(baseUrl)
            .path(path)
            .httpMethod(httpMethod)
            .bodyArg(requestBodyArgName)
            .queryParams(queryParams)
            .defaultHeaders(defaultHeaders)
            .headers(headers)
            .build();

    return apiToolCallback;
  }

  private MediaType getContentType(final RequestBody requestBody) {
    final Content content = requestBody.getContent();
    Set<String> contentTypes = content.keySet();
    if (CollectionUtils.isNotEmpty(contentTypes)) {
      if (contentTypes.contains(APPLICATION_JSON_VALUE)) {
        return MediaType.APPLICATION_JSON;
      } else {
        String contentType = contentTypes.stream().findFirst().get();
        return MediaType.valueOf(contentType);
      }
    } else {
      log.error("Request body Content Type is empty or not defined");
      throw new IllegalArgumentException("Request body Content Type is empty or not defined");
    }
  }

  private List<MediaType> getAcceptableMediaTypes(final Operation operation) {
    final List<String> mediaTypes =
        operation.getResponses().values().stream()
            .flatMap(
                apiResponse -> {
                  Content content = apiResponse.getContent();
                  if (content == null) {
                    return Stream.empty();
                  } else {
                    return content.keySet().stream();
                  }
                })
            .distinct()
            .toList();
    return CollectionUtils.isEmpty(mediaTypes)
        ? List.of(MediaType.ALL)
        : mediaTypes.stream().map(MediaType::valueOf).toList();
  }

  private String buildRequestBodySchema(
      final ObjectNode properties,
      final RequestBody requestBody,
      final MediaType contentType,
      final Components components) {

    final Content content = requestBody.getContent();
    final Schema<?> schema = content.get(contentType.toString()).getSchema();

    final String ref = schema.get$ref();
    if (StringUtils.isNotBlank(ref)) {
      final String requestBodyArgClassName = ref.substring(ref.lastIndexOf('/') + 1);
      final String requestBodyArgName =
          requestBodyArgClassName.substring(0, 1).toLowerCase()
              + requestBodyArgClassName.substring(1);
      final Schema<?> bodyArgSchema = components.getSchemas().get(requestBodyArgClassName);

      this.buildPropertySchema(requestBodyArgName, properties, bodyArgSchema, components);
      final String requestBodyDescription = requestBody.getDescription();
      if (StringUtils.isNotBlank(requestBodyDescription)) {
        properties.put(Attributes.DESCRIPTION, requestBodyDescription);
      }
      return requestBodyArgName;
    } else {
      // May need to throw an exception in this case
      log.warn("Request body schema is not defined");
      final String requestBodyArgName = "request";
      this.populateProperties(requestBodyArgName, properties, schema, components);
      return requestBodyArgName;
    }
  }

  private void buildPropertySchema(
      final String propertyName,
      final ObjectNode objectNode,
      final Schema<?> schema,
      final Components components) {
    final String ref = schema.get$ref();
    if (StringUtils.isNotBlank(ref)) {
      final String objectClassName = ref.substring(ref.lastIndexOf('/') + 1);
      final String objectArgName =
          objectClassName.substring(0, 1).toLowerCase() + objectClassName.substring(1);
      final Schema<?> objectSchema = components.getSchemas().get(objectClassName);

      final ObjectNode properties = objectNode.putObject(Attributes.PROPERTIES);
      this.buildPropertySchema(objectArgName, properties, objectSchema, components);
    } else {
      this.populateProperties(propertyName, objectNode, schema, components);
    }
  }

  private void populateProperties(
      String propertyName, ObjectNode objectNode, Schema<?> schema, Components components) {
    final ObjectNode propNode = objectNode.putObject(propertyName);
    this.putType(schema, propNode, components);
    this.putFormat(schema, propNode);
    this.putDescription(schema, propNode);
    this.putEnum(schema, propNode);
    this.putPattern(schema, propNode);
    this.putExample(schema, propNode);

    final Map<String, Schema> subProperties = schema.getProperties();
    if (MapUtils.isNotEmpty(subProperties)) {
      final ObjectNode propertiesNode = propNode.putObject(Attributes.PROPERTIES);
      for (Map.Entry<String, Schema> entry : subProperties.entrySet()) {
        buildPropertySchema(entry.getKey(), propertiesNode, entry.getValue(), components);
      }
    }

    this.putRequiredProperties(schema, propNode);
  }

  private void putDescription(final Schema<?> schema, final ObjectNode objectNode) {
    String description = schema.getDescription();
    if (StringUtils.isNotBlank(description)) {
      objectNode.put(Attributes.DESCRIPTION, description);
    }
  }

  private void putFormat(final Schema<?> schema, final ObjectNode objectNode) {
    String format = schema.getFormat();
    if (StringUtils.isNotBlank(format)) {
      objectNode.put(Attributes.FORMAT, format);
    }
  }

  private void putType(
      final Schema<?> schema, final ObjectNode objectNode, final Components components) {
    final Optional<String> type = this.getDataType(schema);
    if (type.isPresent()) {
      objectNode.put(Attributes.TYPE, type.get());
      if (type.get().equals(Attributes.ARRAY)) {
        Optional<String> itemDataType = this.getDataType(schema.getItems());
        if (itemDataType.isPresent()) {
          ObjectNode itemsNode = objectNode.putObject(Attributes.ITEMS);
          itemsNode.put(Attributes.TYPE, itemDataType.get());
        } else if (StringUtils.isNotBlank(schema.getItems().get$ref())) {
          String ref = schema.getItems().get$ref();
          String itemClassName = ref.substring(ref.lastIndexOf('/') + 1);
          //          String itemArgName =
          //              itemClassName.substring(0, 1).toLowerCase() + itemClassName.substring(1);
          final Schema<?> itemSchema = components.getSchemas().get(itemClassName);
          //          itemsNode.put(Attributes.TYPE, Attributes.OBJECT);
          //          final ObjectNode properties = objectNode.putObject(Attributes.PROPERTIES);
          //          this.putType(schema.getItems(), itemsNode);
          this.buildPropertySchema(Attributes.ITEMS, objectNode, itemSchema, components);
        } else {
          // May need to throw an exception in this case
          log.warn("Items schema is not defined for array type");
        }
      }
    }
  }

  private void putRequiredProperties(final Schema<?> schema, final ObjectNode objectNode) {
    List<String> requiredProperties = schema.getRequired();
    if (CollectionUtils.isNotEmpty(requiredProperties)) {
      var requiredArray = objectNode.putArray(Attributes.REQUIRED);
      requiredProperties.forEach(requiredArray::add);
    }
  }

  private void putExample(final Schema<?> schema, final ObjectNode objectNode) {
    Object example = schema.getExample();
    if (Objects.nonNull(example)) {
      objectNode.putPOJO(Attributes.EXAMPLE, example);
    }
  }

  private void putPattern(final Schema<?> schema, final ObjectNode objectNode) {
    String pattern = schema.getPattern();
    if (StringUtils.isNotBlank(pattern)) {
      objectNode.put(Attributes.PATTERN, pattern);
    }
  }

  private void putEnum(final Schema<?> schema, final ObjectNode objectNode) {
    if (CollectionUtils.isNotEmpty(schema.getEnum())) {
      var enumArray = objectNode.putArray(Attributes.ENUM);
      schema.getEnum().forEach(enumValue -> enumArray.add(enumValue.toString()));
    }
  }

  private Optional<String> getDataType(final Schema<?> schema) {
    if (CollectionUtils.isNotEmpty(schema.getTypes())) {
      return Optional.ofNullable(schema.getTypes().iterator().next());
    } else {
      // May need to throw an exception in this case
      return Optional.empty();
    }
  }

  public enum ParamIn {
    path,
    query,
    header
    //    , cookie
  }

  public static class Attributes {
    public static final String OBJECT = "object";
    public static final String ARRAY = "array";
    public static final String DESCRIPTION = "description";
    public static final String TYPE = "type";
    public static final String FORMAT = "format";
    public static final String REQUIRED = "required";
    public static final String EXAMPLE = "example";
    public static final String PATTERN = "pattern";
    public static final String ENUM = "enum";
    public static final String ITEMS = "items";
    public static final String PROPERTIES = "properties";
    public static final String ADDITIONAL_PROPERTIES = "additionalProperties";
  }
}
