package ai.ksoot.rest.mcp.server.tool.domain.model;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.util.Assert;

public class McpToolCallbackProvider implements ToolCallbackProvider {

  private final Map<String, ToolCallback> apiToolCallbacks;

  private McpToolCallbackProvider(final List<ToolCallback> apiToolCallbacks) {
    Assert.notNull(apiToolCallbacks, "apiToolCallbacks cannot be null");
    Assert.noNullElements(apiToolCallbacks, "apiToolCallbacks cannot contain null elements");
    this.apiToolCallbacks =
        apiToolCallbacks.stream()
            .collect(
                toMap(
                    ToolCallback -> ToolCallback.getToolDefinition().name(),
                    ToolCallback -> ToolCallback));
  }

  public static McpToolCallbackProvider of(final List<ToolCallback> apiToolCallbacks) {
    return new McpToolCallbackProvider(apiToolCallbacks);
  }

  public static McpToolCallbackProvider of(
      final ToolCallback toolCallback, final ToolCallback... apiToolCallbacks) {
    Assert.notNull(toolCallback, "'toolCallback' cannot be null");
    Assert.noNullElements(apiToolCallbacks, "'apiToolCallbacks' cannot contain null elements");
    if (ArrayUtils.isNotEmpty(apiToolCallbacks)) {
      ToolCallback[] tools = ArrayUtils.add(apiToolCallbacks, toolCallback);
      return new McpToolCallbackProvider(Arrays.asList(tools));
    } else {
      return new McpToolCallbackProvider(List.of(toolCallback));
    }
  }

  public void addApiTools(final List<ToolCallback> apiToolCallbacks) {
    Assert.notNull(apiToolCallbacks, "'toolCallback' cannot be null");
    Assert.noNullElements(apiToolCallbacks, "'apiToolCallbacks' cannot contain null elements");
    final Map<String, ToolCallback> tools =
        apiToolCallbacks.stream()
            .collect(
                toMap(
                    toolCallback -> toolCallback.getToolDefinition().name(),
                    toolCallback -> toolCallback));
    this.apiToolCallbacks.putAll(tools);
  }

  public void addApiTools(final ToolCallback toolCallback, final ToolCallback... apiToolCallbacks) {
    Assert.notNull(toolCallback, "'toolCallback' cannot be null");
    Assert.noNullElements(apiToolCallbacks, "'apiToolCallbacks' cannot contain null elements");
    final List<ToolCallback> tools = new ArrayList<>();
    tools.add(toolCallback);
    tools.addAll(Arrays.asList(apiToolCallbacks));
    this.addApiTools(tools);
  }

  public void addApiTool(final ToolCallback toolCallback) {
    Assert.notNull(toolCallback, "'toolCallback' cannot be null");
    this.apiToolCallbacks.put(toolCallback.getToolDefinition().name(), toolCallback);
  }

  // Update tools mechanism is exactly same as add tools, added just for verbosity and clarity
  public void updateApiTools(final List<ToolCallback> apiToolCallbacks) {
    Assert.notNull(apiToolCallbacks, "'toolCallback' cannot be null");
    Assert.noNullElements(apiToolCallbacks, "'apiToolCallbacks' cannot contain null elements");
    final Map<String, ToolCallback> tools =
        apiToolCallbacks.stream()
            .collect(
                toMap(
                    toolCallback -> toolCallback.getToolDefinition().name(),
                    toolCallback -> toolCallback));
    this.apiToolCallbacks.putAll(tools);
  }

  public void updateApiTools(
      final ToolCallback toolCallback, final ToolCallback... apiToolCallbacks) {
    Assert.notNull(toolCallback, "'toolCallback' cannot be null");
    Assert.noNullElements(apiToolCallbacks, "'apiToolCallbacks' cannot contain null elements");
    final List<ToolCallback> tools = new ArrayList<>();
    tools.add(toolCallback);
    tools.addAll(Arrays.asList(apiToolCallbacks));
    this.addApiTools(tools);
  }

  public void updateApiTool(final ToolCallback toolCallback) {
    Assert.notNull(toolCallback, "'toolCallback' cannot be null");
    this.apiToolCallbacks.put(toolCallback.getToolDefinition().name(), toolCallback);
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
