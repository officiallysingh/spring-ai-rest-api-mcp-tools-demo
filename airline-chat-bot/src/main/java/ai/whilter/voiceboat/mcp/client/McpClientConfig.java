package ai.whilter.voiceboat.mcp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Configuration
public class McpClientConfig {

  //    @Bean
  //    ChatClient chatClient(
  //            final ChatClient.Builder chatClientBuilder, final McpSyncClient mcpSyncClient) {
  //        return chatClientBuilder
  //                .defaultSystem(Prompts.SYSTEM)
  //                .defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClient))
  //                .defaultAdvisors(
  //                        new PromptChatMemoryAdvisor(new InMemoryChatMemory()), new
  // SimpleLoggerAdvisor())
  //                .build();
  //    }
  @Bean
  @Lazy
  ChatClient chatClient(
      final ChatClient.Builder chatClientBuilder,
      final SyncMcpToolCallbackProvider syncMcpToolCallbackProvider
      //            ,final ToolCallbackProvider toolCallbackProvider
      ) {
    return chatClientBuilder
        .defaultSystem(Prompts.SYSTEM)
        .defaultTools(syncMcpToolCallbackProvider)
        //                .defaultTools(toolCallbackProvider)
        .defaultAdvisors(
            new PromptChatMemoryAdvisor(new InMemoryChatMemory()), new SimpleLoggerAdvisor())
        .build();
  }

  //    @Bean
  //    ToolCallbackResolver toolCallbackResolver(GenericApplicationContext applicationContext,
  //                                              List<FunctionCallback> functionCallbacks,
  // List<ToolCallbackProvider> tcbProviders, final RealtimeToolCallbackResolver
  // realtimeToolCallbackResolver) {
  //
  //        List<FunctionCallback> allFunctionAndToolCallbacks = new ArrayList<>(functionCallbacks);
  //        tcbProviders.stream().map(pr ->
  // List.of(pr.getToolCallbacks())).forEach(allFunctionAndToolCallbacks::addAll);
  //
  //        var staticToolCallbackResolver = new
  // StaticToolCallbackResolver(allFunctionAndToolCallbacks);
  //
  //        var springBeanToolCallbackResolver = SpringBeanToolCallbackResolver.builder()
  //                .applicationContext(applicationContext)
  //                .build();
  //
  //        return new DelegatingToolCallbackResolver(List.of(staticToolCallbackResolver,
  // springBeanToolCallbackResolver, realtimeToolCallbackResolver));
  //    }
}
