package ai.ksoot.rest.mcp.server.tool.domain.model;

import java.util.List;
import org.springframework.ai.tool.ToolCallback;

public record ApiToolChangeEvent(List<ToolCallback> toolCallbacks, Type type) {

  public static ApiToolChangeEvent of(final ToolCallback toolCallback, Type type) {
    return new ApiToolChangeEvent(List.of(toolCallback), type);
  }

  public static ApiToolChangeEvent of(final List<ToolCallback> toolCallbacks, Type type) {
    return new ApiToolChangeEvent(toolCallbacks, type);
  }

  public static ApiToolChangeEvent ofCreate(final ToolCallback toolCallback) {
    return new ApiToolChangeEvent(List.of(toolCallback), Type.CREATE);
  }

  public static ApiToolChangeEvent ofCreate(final List<ToolCallback> toolCallbacks) {
    return new ApiToolChangeEvent(toolCallbacks, Type.CREATE);
  }

  public static ApiToolChangeEvent ofUpdate(final ToolCallback toolCallback) {
    return new ApiToolChangeEvent(List.of(toolCallback), Type.UPDATE);
  }

  public static ApiToolChangeEvent ofUpdate(final List<ToolCallback> toolCallbacks) {
    return new ApiToolChangeEvent(toolCallbacks, Type.UPDATE);
  }

  public static ApiToolChangeEvent ofDelete(final ToolCallback toolCallback) {
    return new ApiToolChangeEvent(List.of(toolCallback), Type.DELETE);
  }

  public static ApiToolChangeEvent ofDelete(final List<ToolCallback> toolCallbacks) {
    return new ApiToolChangeEvent(toolCallbacks, Type.DELETE);
  }

  public enum Type {
    CREATE,
    UPDATE,
    DELETE
  }
}
