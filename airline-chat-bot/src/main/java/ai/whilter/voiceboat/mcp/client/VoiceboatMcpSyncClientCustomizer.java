// package ai.whilter.voiceboat.mcp.client;
//
// import io.modelcontextprotocol.client.McpClient;
// import io.modelcontextprotocol.spec.McpSchema;
// import java.util.List;
// import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.stereotype.Component;
//
// @Component
// public class VoiceboatMcpSyncClientCustomizer implements McpSyncClientCustomizer {
//
//  private final RealtimeToolCallbackResolver realtimeToolCallbackResolver;
//
//  public VoiceboatMcpSyncClientCustomizer(
//      @Qualifier("realtimeToolCallbackResolver")
//          final RealtimeToolCallbackResolver realtimeToolCallbackResolver) {
//    this.realtimeToolCallbackResolver = realtimeToolCallbackResolver;
//  }
//
//  @Override
//  public void customize(final String name, final McpClient.SyncSpec spec) {
//
//    // Adds a consumer to be notified when the available tools change, such as tools
//    // being added or removed.
//    spec.toolsChangeConsumer(
//        (List<McpSchema.Tool> tools) -> {
//          tools.forEach(System.out::println);
//          // Handle tools change
//          this.realtimeToolCallbackResolver.addToolCallbacks(tools);
//        });
//  }
// }
