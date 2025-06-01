// package ai.whilter.voiceboat.mcp.client;
//
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
//
// import io.modelcontextprotocol.client.McpSyncClient;
// import io.modelcontextprotocol.spec.McpSchema;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.ai.mcp.SyncMcpToolCallback;
// import org.springframework.ai.tool.ToolCallback;
// import org.springframework.ai.tool.ToolCallbacks;
// import org.springframework.ai.tool.resolution.ToolCallbackResolver;
// import org.springframework.beans.factory.ObjectProvider;
// import org.springframework.core.Ordered;
// import org.springframework.stereotype.Component;
// import org.springframework.util.Assert;
//
// @Component(value = "realtimeToolCallbackResolver")
// @Slf4j
// public class RealtimeToolCallbackResolver implements ToolCallbackResolver, Ordered {
//
////  private final ObjectProvider<McpSyncClient>
//
//  private static final Map<String, ToolCallback> toolCallbacksCache = new ConcurrentHashMap<>();
//
//  @Override
//  public ToolCallback resolve(final String toolName) {
//    Assert.hasText(toolName, "toolName cannot be null or empty");
//
//    log.debug("ToolCallback resolution attempt from RealtimeToolCallbackResolver");
//
//    ToolCallback resolvedToolCallback = toolCallbacksCache.get(toolName);
//    if (resolvedToolCallback != null) {
//      log.debug("ToolCallback found in cache for toolName: {}", toolName);
//      return resolvedToolCallback;
//    } else {
//
//      log.debug("ToolCallback not found in cache for toolName: {}", toolName);
//      return null;
//    }
//  }
//
//  @Override
//  public int getOrder() {
//    return Integer.MAX_VALUE;
//  }
//
//  public void addToolCallbacks(final List<McpSchema.Tool> tools) {
////    Assert.hasText(toolName, "toolName cannot be null or empty");
////    Assert.notNull(toolCallback, "toolCallback cannot be null");
//
////    log.debug("Adding ToolCallback to cache for toolName: {}", toolName);
////    new SyncMcpToolCallback()
//    Arrays.stream(ToolCallbacks.from(tools)).forEach(tool -> {;
//      if (this.toolCallbacksCache.containsKey(tool.getToolDefinition().name())) {
//        log.debug("ToolCallback already exists in cache for toolName: {}",
// tool.getToolDefinition().name());
//      } else {
//        log.debug("Adding ToolCallback to cache for toolName: {}",
// tool.getToolDefinition().name());
//        this.toolCallbacksCache.put(tool.getToolDefinition().name(), tool);
//      }
//    });
//  }
// }
