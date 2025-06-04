package ai.ksoot.rest.mcp.server.tool.domain.service;

import ai.ksoot.rest.mcp.server.adapter.repository.ApiToolCallbackRepository;
import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolCallback;
import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolRequest;
import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolUpdateRequest;
import com.ksoot.problem.core.Problems;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiToolService {

  private final ApiToolCallbackRepository apiToolCallbackRepository;

  @Transactional(readOnly = true)
  public boolean apiToolExists(final String name) {
    return this.apiToolCallbackRepository.existsByToolDefinitionName(name);
  }

  @Transactional
  public ApiToolCallback createApiTool(final ApiToolRequest request) {
    final ApiToolCallback apiTool =
        ApiToolCallback.tool(request.getName(), request.getDescription(), request.getInputSchema())
            .baseUrl(request.getBaseUrl())
            .path(request.getPath())
            .httpMethod(request.getHttpMethod())
            .bodyArg(request.getBodyArg())
            .queryParams(request.getQueryParams())
            .defaultHeaders(request.getDefaultHeaders())
            .headers(request.getHeaders())
            .build();
    return this.apiToolCallbackRepository.save(apiTool);
  }

  @Transactional
  public List<ApiToolCallback> createApiTools(final List<ApiToolRequest> requests) {
    final List<ApiToolCallback> apiTools =
        requests.stream()
            .map(
                request ->
                    ApiToolCallback.tool(
                            request.getName(), request.getDescription(), request.getInputSchema())
                        .baseUrl(request.getBaseUrl())
                        .path(request.getPath())
                        .httpMethod(request.getHttpMethod())
                        .bodyArg(request.getBodyArg())
                        .queryParams(request.getQueryParams())
                        .defaultHeaders(request.getDefaultHeaders())
                        .build())
            .toList();

    return this.apiToolCallbackRepository.saveAll(apiTools);
  }

  @Transactional
  public ApiToolCallback updateApiToolById(final String id, final ApiToolUpdateRequest request) {

    final ApiToolCallback apiToolCallback = this.getApiToolById(id);
    this.updateApiTool(apiToolCallback, request);
    return this.apiToolCallbackRepository.save(apiToolCallback);
  }

  @Transactional
  public ApiToolCallback updateApiToolByName(
      final String name, final ApiToolUpdateRequest request) {
    final ApiToolCallback apiToolCallback = this.getApiToolByName(name);
    this.updateApiTool(apiToolCallback, request);
    return this.apiToolCallbackRepository.save(apiToolCallback);
  }

  private void updateApiTool(
      final ApiToolCallback apiToolCallback, final ApiToolUpdateRequest request) {
    Optional.ofNullable(request.getName()).ifPresent(apiToolCallback::setName);
    Optional.ofNullable(request.getDescription()).ifPresent(apiToolCallback::setDescription);
    Optional.ofNullable(request.getInputSchema()).ifPresent(apiToolCallback::setInputSchema);
    Optional.ofNullable(request.getReturnDirect()).ifPresent(apiToolCallback::setReturnDirect);
    Optional.ofNullable(request.getHttpMethod()).ifPresent(apiToolCallback::setHttpMethod);
    Optional.ofNullable(request.getDefaultHeaders()).ifPresent(apiToolCallback::setDefaultHeaders);
    Optional.ofNullable(request.getBaseUrl()).ifPresent(apiToolCallback::setBaseUrl);
    Optional.ofNullable(request.getPath()).ifPresent(apiToolCallback::setPath);
    Optional.ofNullable(request.getQueryParams()).ifPresent(apiToolCallback::setQueryParams);
    Optional.ofNullable(request.getHeaders()).ifPresent(apiToolCallback::setHeaders);
    Optional.ofNullable(request.getBodyArg()).ifPresent(apiToolCallback::setBodyArg);
  }

  @Transactional(readOnly = true)
  public ApiToolCallback getApiToolById(final String id) {
    return this.apiToolCallbackRepository.findById(id).orElseThrow(Problems::notFound);
  }

  @Transactional(readOnly = true)
  public ApiToolCallback getApiToolByName(final String name) {
    return this.apiToolCallbackRepository
        .findByToolDefinitionName(name)
        .orElseThrow(Problems::notFound);
  }

  @Transactional(readOnly = true)
  public List<ApiToolCallback> getAllApiTools() {
    return this.apiToolCallbackRepository.findAll();
  }

  //  @Transactional
  //  public ApiToolCallback updateApiTool(final String id, final ApiToolUpdationRQ request) {
  //    final ApiToolCallback apiTool =
  // this.apiToolCallbackRepository.findById(id).orElseThrow(Problems::notFound);
  //
  //    Optional.ofNullable(request.getName()).ifPresent(apiTool::setName);
  //
  //    return this.apiToolCallbackRepository.save(apiTool);
  //  }

  @Transactional
  public ApiToolCallback deleteApiToolById(final String id) {
    if (!this.apiToolCallbackRepository.existsById(id)) {
      throw Problems.notFound();
    }
    ApiToolCallback apiToolCallback =
        this.apiToolCallbackRepository.findById(id).orElseThrow(Problems::notFound);
    this.apiToolCallbackRepository.deleteById(id);
    return apiToolCallback;
  }

  @Transactional
  public ApiToolCallback deleteApiToolByName(final String name) {
    ApiToolCallback apiToolCallback =
        this.apiToolCallbackRepository
            .findByToolDefinitionName(name)
            .orElseThrow(Problems::notFound);
    this.apiToolCallbackRepository.deleteByToolDefinitionName(name);
    return apiToolCallback;
  }

  @Transactional(readOnly = true)
  public Page<ApiToolCallback> getApiTools(final Pageable pageRequest) {
    return this.apiToolCallbackRepository.findAll(pageRequest);
  }
}
