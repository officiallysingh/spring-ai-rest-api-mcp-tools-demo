package ai.ksoot.rest.mcp.server.adapter.controller;

import static ai.ksoot.rest.mcp.server.common.CommonConstants.DEFAULT_PAGE_SIZE;
import static ai.ksoot.rest.mcp.server.common.util.rest.ApiConstants.*;
import static ai.ksoot.rest.mcp.server.common.util.rest.ApiStatus.*;

import ai.ksoot.rest.mcp.server.common.util.pagination.PaginatedResource;
import ai.ksoot.rest.mcp.server.common.util.rest.Api;
import ai.ksoot.rest.mcp.server.common.util.rest.response.APIResponse;
import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolRequest;
import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/v1/voiceboat/api-tools")
@Tag(name = "API Tools", description = "management APIs.")
public interface ApiToolApi extends Api {

  @Operation(
      operationId = "api-tool-exists-by-name",
      summary = "Check if a API Tool with given name exists")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = SC_200,
            description = "Returns true or false if API Tool exists or not respectively")
      })
  @GetMapping(path = "/{name}/exists", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Boolean> apiToolExists(
      @Parameter(description = "API Tool name", required = true, example = "search_flights")
          @PathVariable(name = "name")
          @NotEmpty
          final String name);

  @Operation(operationId = "create-api-tool", summary = "Creates an API Tool")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = SC_201, description = "API Tool created successfully"),
        @ApiResponse(
            responseCode = SC_400,
            description = "Bad request",
            content = @Content(examples = @ExampleObject(BAD_REQUEST_EXAMPLE_RESPONSE)))
      })
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<APIResponse<?>> createApiTool(
      @Parameter(description = "Create API tool request", required = true) @RequestBody @Valid
          final ApiToolRequest request);

  @Operation(operationId = "create-api-tools", summary = "Create API Tools")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = SC_201, description = "API Tools created successfully"),
        @ApiResponse(
            responseCode = SC_400,
            description = "Bad request",
            content = @Content(examples = @ExampleObject(BAD_REQUEST_EXAMPLE_RESPONSE)))
      })
  @PostMapping(
      path = "/all",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<APIResponse<?>> createApiTools(
      @Parameter(description = "Create API tools request", required = true)
          @RequestBody
          @Valid
          @NotEmpty
          final List<@Valid ApiToolRequest> request);

  @Operation(operationId = "get-api-tool-by-id", summary = "Gets an API Tool by id")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = SC_200, description = "API Tool returned successfully"),
        @ApiResponse(
            responseCode = SC_404,
            description = "Requested API Tool not found",
            content = @Content(examples = @ExampleObject(NOT_FOUND_EXAMPLE_RESPONSE)))
      })
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ApiToolResponse> getApiToolById(
      @Parameter(description = "API Tool Id", required = true, example = "68106d632e1b8a3710692a85")
          @PathVariable(name = "id")
          final String id);

  @Operation(operationId = "get-api-tool-by-name", summary = "Gets an API Tool by Name")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = SC_200, description = "API Tool returned successfully"),
        @ApiResponse(
            responseCode = SC_404,
            description = "Requested API Tool not found",
            content = @Content(examples = @ExampleObject(NOT_FOUND_EXAMPLE_RESPONSE)))
      })
  @GetMapping(path = "/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ApiToolResponse> getApiToolByName(
      @Parameter(description = "API Tool Name", required = true, example = "search_flights")
          @PathVariable(name = "name")
          final String name);

  @Operation(operationId = "get-all-api-tools", summary = "Get all API Tools")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description =
                "All API Tools list returned successfully. Returns an empty list if no records found")
      })
  @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<ApiToolResponse>> getAllApiTools();

  //  @Operation(operationId = "update-api-tool", summary = "Updates an API Tool")
  //  @ApiResponses(
  //      value = {
  //        @ApiResponse(responseCode = "200", description = "API Tool updated successfully"),
  //        @ApiResponse(
  //            responseCode = SC_400,
  //            description = "Bad request",
  //            content = @Content(examples = @ExampleObject(BAD_REQUEST_EXAMPLE_RESPONSE))),
  //        @ApiResponse(
  //            responseCode = "404",
  //            description = "Requested API Tool not found",
  //            content = @Content(examples = @ExampleObject(NOT_FOUND_EXAMPLE_RESPONSE)))
  //      })
  //  @PatchMapping(
  //      path = "/{id}",
  //      consumes = MediaType.APPLICATION_JSON_VALUE,
  //      produces = MediaType.APPLICATION_JSON_VALUE)
  //  ResponseEntity<APIResponse<?>> updateApiTool(
  //      @Parameter(description = "API Tool Id", required = true, example =
  // "68106d632e1b8a3710692a85")
  //          @PathVariable(name = "id")
  //          final String id,
  //      @Parameter(description = "Update API tool request", required = true) @RequestBody @Valid
  //          final ApiToolUpdationRQ request);

  @Operation(operationId = "delete-tool-by-id", summary = "Deletes a Tool by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = SC_200,
            description = "API Tool deleted successfully",
            content = @Content(examples = @ExampleObject(RECORD_DELETED_RESPONSE))),
        @ApiResponse(
            responseCode = SC_404,
            description = "Requested API Tool not found",
            content = @Content(examples = @ExampleObject(NOT_FOUND_EXAMPLE_RESPONSE)))
      })
  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<APIResponse<?>> deleteApiToolById(
      @Parameter(description = "API Tool Id", required = true, example = "68106d632e1b8a3710692a85")
          @PathVariable(name = "id")
          final String id);

  @Operation(operationId = "delete-tool-by-name", summary = "Deletes a Tool by name")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = SC_200,
            description = "API Tool deleted successfully",
            content = @Content(examples = @ExampleObject(RECORD_DELETED_RESPONSE))),
        @ApiResponse(
            responseCode = SC_404,
            description = "Requested API Tool not found",
            content = @Content(examples = @ExampleObject(NOT_FOUND_EXAMPLE_RESPONSE)))
      })
  @DeleteMapping(path = "/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<APIResponse<?>> deleteApiToolByName(
      @Parameter(description = "API Tool Name", required = true, example = "search_flights")
          @PathVariable(name = "name")
          final String name);

  @Operation(operationId = "get-api-tools", summary = "Gets a Page of API Tools")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = SC_200,
            description =
                "API Tools page returned successfully. Returns an empty Page if no records found")
      })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  PaginatedResource<ApiToolResponse> getApiTools(
      @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageRequest);

  @Operation(operationId = "parse-openapi-spec", summary = "Parse OpenAPI spec")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = SC_200,
            description = "API Tool definitions parsed successfully"),
        @ApiResponse(
            responseCode = SC_400,
            description = "Bad request",
            content = @Content(examples = @ExampleObject(BAD_REQUEST_EXAMPLE_RESPONSE)))
      })
  @PostMapping(
      path = "/parse-openapi-spec",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<ApiToolResponse>> parseOpenApiSpec(
      @Parameter(
              description = "OpenAPI Specification file. Json or YML",
              required = true,
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestParam
          final MultipartFile file)
      throws IOException;
}
