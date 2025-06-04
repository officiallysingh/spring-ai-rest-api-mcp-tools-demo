package ai.ksoot.rest.mcp.server.tool.domain.service;

import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolChangeEvent;
import ai.ksoot.rest.mcp.server.tool.domain.model.McpToolCallbackProvider;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolChangeListener {

  @Lazy @Autowired private McpSyncServer mcpSyncServer;

  private final McpToolCallbackProvider mcpToolCallbackProvider;

//  @Transactional
//  @EventListener
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  void handleToolChangeEvent(
      final ApiToolChangeEvent apiToolChangeEvent) {
    final List<ToolCallback> toolCallbacks = apiToolChangeEvent.toolCallbacks();
    final List<String> toolNames =
        toolCallbacks.stream()
            .map(ToolCallback::getToolDefinition)
            .map(ToolDefinition::name)
            .toList();
    switch (apiToolChangeEvent.type()) {
      case CREATE:
        log.info("Handling tool creation event for tools: {}", toolNames);
        this.mcpToolCallbackProvider.addApiTools(toolCallbacks);
        final List<McpServerFeatures.SyncToolSpecification> newToolSpecifications =
            McpToolUtils.toSyncToolSpecification(toolCallbacks);
        newToolSpecifications.forEach(this.mcpSyncServer::addTool);
        break;
      case UPDATE:
        log.info("Handling tool update event for tools: {}", toolNames);
        this.mcpToolCallbackProvider.updateApiTools(toolCallbacks);
        final List<McpServerFeatures.SyncToolSpecification> updatedToolSpecifications =
            McpToolUtils.toSyncToolSpecification(toolCallbacks);
        updatedToolSpecifications.forEach(
            updatedToolSpecification -> {
              this.mcpSyncServer.removeTool(updatedToolSpecification.tool().name());
              this.mcpSyncServer.addTool(updatedToolSpecification);
            });
        break;
      case DELETE:
        log.info("Handling tool deletion event for tools: {}", toolNames);
        toolCallbacks.forEach(
            apiToolCallback -> {
              this.mcpToolCallbackProvider.removeApiTool(
                  apiToolCallback.getToolDefinition().name());
              this.mcpSyncServer.removeTool(apiToolCallback.getToolDefinition().name());
            });
        break;
    }
  }
}
