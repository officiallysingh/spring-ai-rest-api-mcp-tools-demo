package ai.ksoot.rest.mcp.server.adapter.controller;

import static ai.ksoot.rest.mcp.server.common.CommonErrorKeys.EMPTY_UPDATE_REQUEST;
import static ai.ksoot.rest.mcp.server.tool.domain.ApiToolMappers.API_TOOLS_LIST_TRANSFORMER;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import ai.ksoot.rest.mcp.server.common.util.GeneralMessageResolver;
import ai.ksoot.rest.mcp.server.common.util.pagination.PaginatedResource;
import ai.ksoot.rest.mcp.server.common.util.pagination.PaginatedResourceAssembler;
import ai.ksoot.rest.mcp.server.common.util.rest.response.APIResponse;
import ai.ksoot.rest.mcp.server.tool.domain.ApiToolMappers;
import ai.ksoot.rest.mcp.server.tool.domain.model.*;
import ai.ksoot.rest.mcp.server.tool.domain.service.ApiToolService;
import ai.ksoot.rest.mcp.server.tool.domain.service.OpenApiSpecParser;
import com.ksoot.problem.core.Problems;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
class ApiToolController implements ApiToolApi {

  private final ApiToolService apiToolService;

  private final OpenApiSpecParser openApiSpecParser;

  @Override
  public ResponseEntity<Boolean> apiToolExists(final String name) {
    return ResponseEntity.ok(this.apiToolService.apiToolExists(name));
  }

  @Override
  public ResponseEntity<APIResponse<?>> createApiTool(final ApiToolRequest request) {
    final ApiToolCallback toolCallback = this.apiToolService.createApiTool(request);

    return ResponseEntity.created(
            linkTo(methodOn(ApiToolController.class).getApiToolById(toolCallback.getId()))
                .withSelfRel()
                .toUri())
        .body(APIResponse.newInstance().addSuccess(GeneralMessageResolver.RECORD_CREATED));
  }

  @Override
  public ResponseEntity<APIResponse<?>> createApiTools(List<ApiToolRequest> requests) {
    final List<ApiToolCallback> toolCallbacks = this.apiToolService.createApiTools(requests);

    return ResponseEntity.created(
            linkTo(methodOn(ApiToolController.class).getAllApiTools()).withSelfRel().toUri())
        .body(APIResponse.newInstance().addSuccess(GeneralMessageResolver.RECORD_CREATED));
  }

  @Override
  public ResponseEntity<ApiToolResponse> getApiToolById(final String id) {
    return ResponseEntity.ok(
        ApiToolMappers.INSTANCE.toApiToolResponse(this.apiToolService.getApiToolById(id)));
  }

  @Override
  public ResponseEntity<ApiToolResponse> getApiToolByName(final String name) {
    return ResponseEntity.ok(
        ApiToolMappers.INSTANCE.toApiToolResponse(this.apiToolService.getApiToolByName(name)));
  }

  @Override
  public ResponseEntity<List<ApiToolResponse>> getAllApiTools() {
    final List<ApiToolCallback> apiTools = this.apiToolService.getAllApiTools();
    return ResponseEntity.ok(API_TOOLS_LIST_TRANSFORMER.apply(apiTools));
  }

  @Override
  public ResponseEntity<APIResponse<?>> updateApiToolById(
      final String id, final ApiToolUpdateRequest request) {
    if (request.isEmpty()) {
      throw Problems.newInstance(EMPTY_UPDATE_REQUEST).throwAble(HttpStatus.BAD_REQUEST);
    }
    final ApiToolCallback apiToolCallback = this.apiToolService.updateApiToolById(id, request);
    return ResponseEntity.ok()
        .location(
            linkTo(methodOn(ApiToolController.class).getApiToolById(apiToolCallback.getId()))
                .withSelfRel()
                .toUri())
        .body(APIResponse.newInstance().addSuccess(GeneralMessageResolver.RECORD_UPDATED));
  }

  @Override
  public ResponseEntity<APIResponse<?>> updateApiToolByName(
      final String name, final ApiToolUpdateRequest request) {
    if (request.isEmpty()) {
      throw Problems.newInstance(EMPTY_UPDATE_REQUEST).throwAble(HttpStatus.BAD_REQUEST);
    }
    final ApiToolCallback apiToolCallback = this.apiToolService.updateApiToolByName(name, request);
    return ResponseEntity.ok()
        .location(
            linkTo(methodOn(ApiToolController.class).getApiToolById(apiToolCallback.getId()))
                .withSelfRel()
                .toUri())
        .body(APIResponse.newInstance().addSuccess(GeneralMessageResolver.RECORD_UPDATED));
  }

  @Override
  public ResponseEntity<APIResponse<?>> deleteApiToolById(final String id) {
    final ApiToolCallback apiToolCallback = this.apiToolService.deleteApiToolById(id);

    return ResponseEntity.ok(
        APIResponse.newInstance().addSuccess(GeneralMessageResolver.RECORD_DELETED));
  }

  @Override
  public ResponseEntity<APIResponse<?>> deleteApiToolByName(final String name) {
    final ApiToolCallback apiToolCallback = this.apiToolService.deleteApiToolByName(name);

    return ResponseEntity.ok(
        APIResponse.newInstance().addSuccess(GeneralMessageResolver.RECORD_DELETED));
  }

  @Override
  public PaginatedResource<ApiToolResponse> getApiTools(final Pageable pageRequest) {
    final Page<ApiToolCallback> apiToolsPage = this.apiToolService.getApiTools(pageRequest);
    return PaginatedResourceAssembler.assemble(apiToolsPage, API_TOOLS_LIST_TRANSFORMER);
  }

  @Override
  public ResponseEntity<List<ApiToolResponse>> parseOpenApiSpec(final MultipartFile file)
      throws IOException {
    final List<ApiToolCallback> apiTools = this.openApiSpecParser.parse(file.getResource());
    return ResponseEntity.ok(API_TOOLS_LIST_TRANSFORMER.apply(apiTools));
  }
}
