package ai.whilter.voiceboat.mcp.server.tool.domain.model;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.util.Assert;

public class ApiToolCallbackProvider implements ToolCallbackProvider {

  private final Map<String, ApiToolCallback> apiToolCallbacks;

  private ApiToolCallbackProvider(final List<ApiToolCallback> apiToolCallbacks) {
    Assert.notNull(apiToolCallbacks, "apiToolCallbacks cannot be null");
    Assert.noNullElements(apiToolCallbacks, "apiToolCallbacks cannot contain null elements");
    this.apiToolCallbacks =
        apiToolCallbacks.stream()
            .collect(
                toMap(
                    apiToolCallback -> apiToolCallback.getToolDefinition().name(),
                    apiToolCallback -> apiToolCallback));
  }

  public static ApiToolCallbackProvider of(final List<ApiToolCallback> apiToolCallbacks) {
    return new ApiToolCallbackProvider(apiToolCallbacks);
  }

  public static ApiToolCallbackProvider of(
      final ApiToolCallback apiToolCallback, final ApiToolCallback... apiToolCallbacks) {
    Assert.notNull(apiToolCallback, "'apiToolCallback' cannot be null");
    Assert.noNullElements(apiToolCallbacks, "'apiToolCallbacks' cannot contain null elements");
    if (ArrayUtils.isNotEmpty(apiToolCallbacks)) {
      ApiToolCallback[] tools = ArrayUtils.add(apiToolCallbacks, apiToolCallback);
      return new ApiToolCallbackProvider(Arrays.asList(tools));
    } else {
      return new ApiToolCallbackProvider(List.of(apiToolCallback));
    }
  }

  public void addApiTools(final List<ApiToolCallback> apiToolCallbacks) {
    Assert.notNull(apiToolCallbacks, "'apiToolCallback' cannot be null");
    Assert.noNullElements(apiToolCallbacks, "'apiToolCallbacks' cannot contain null elements");
    final Map<String, ApiToolCallback> tools =
        apiToolCallbacks.stream()
            .collect(
                toMap(
                    apiToolCallback -> apiToolCallback.getToolDefinition().name(),
                    apiToolCallback -> apiToolCallback));
    this.apiToolCallbacks.putAll(tools);
  }

  public void addApiTools(
      final ApiToolCallback apiToolCallback, final ApiToolCallback... apiToolCallbacks) {
    Assert.notNull(apiToolCallback, "'apiToolCallback' cannot be null");
    Assert.noNullElements(apiToolCallbacks, "'apiToolCallbacks' cannot contain null elements");
    final List<ApiToolCallback> tools = new ArrayList<>();
    tools.add(apiToolCallback);
    tools.addAll(Arrays.asList(apiToolCallbacks));
    this.addApiTools(tools);
  }

  public void addApiTool(final ApiToolCallback apiToolCallback) {
    Assert.notNull(apiToolCallback, "'apiToolCallback' cannot be null");
    this.apiToolCallbacks.put(apiToolCallback.getToolDefinition().name(), apiToolCallback);
  }

  public void removeApiTool(final String toolName) {
    Assert.hasText(toolName, "'toolName' cannot be null");
    this.apiToolCallbacks.remove(toolName);
  }

  public void removeApiTools(final String toolName, final String... toolNames) {
    Assert.hasText(toolName, "'toolName' cannot be null");
    Assert.noNullElements(toolNames, "'toolNames' cannot contain null elements");
    final List<String> tools = new ArrayList<>();
    tools.add(toolName);
    tools.addAll(Arrays.asList(toolNames));
    this.removeApiTools(tools);
  }

  public void removeApiTools(final List<String> toolNames) {
    Assert.noNullElements(toolNames, "'toolNames' cannot contain null elements");
    toolNames.forEach(this.apiToolCallbacks::remove);
  }

  @Override
  public ToolCallback[] getToolCallbacks() {
    return this.apiToolCallbacks.values().toArray(new ToolCallback[this.apiToolCallbacks.size()]);
  }
}
