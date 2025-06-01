package ai.ksoot.mcp.airline.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class McpClientConfig {

  @Bean
  ChatClient chatClient(
      final ChatClient.Builder chatClientBuilder, final ToolCallbackProvider tools) {
    return chatClientBuilder
        .defaultSystem(Prompts.SYSTEM)
        .defaultToolCallbacks(tools.getToolCallbacks())
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build(),
            new SimpleLoggerAdvisor())
        .build();
  }
  //  @Bean
  //  @Lazy
  //  ChatClient chatClient(
  //      final ChatClient.Builder chatClientBuilder,
  //      final SyncMcpToolCallbackProvider syncMcpToolCallbackProvider,
  //      final ChatMemoryRepository chatMemoryRepository
  //      //            ,final ToolCallbackProvider toolCallbackProvider
  //      ) {
  //    //    ChatMemory chatMemory =
  //    //        MessageWindowChatMemory.builder()
  //    //            .chatMemoryRepository(chatMemoryRepository)
  //    //            .maxMessages(10)
  //    //            .build();
  //    return chatClientBuilder
  //        .defaultSystem(Prompts.SYSTEM)
  //        .defaultTools(syncMcpToolCallbackProvider)
  //        //                .defaultTools(toolCallbackProvider)
  //        .defaultAdvisors(
  //            MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build(),
  //            new SimpleLoggerAdvisor())
  //        .build();
  //  }

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
