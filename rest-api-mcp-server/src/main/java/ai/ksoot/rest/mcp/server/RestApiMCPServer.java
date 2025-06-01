package ai.ksoot.rest.mcp.server;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableMongock
public class RestApiMCPServer {

  public static void main(String[] args) {
    SpringApplication.run(RestApiMCPServer.class, args);
  }

  //  @Bean
  //  public ToolCallbackProvider weatherTools(WeatherService weatherService) {
  //    return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
  //  }

  //  @Bean
  //  public ToolCallbackProvider airlineTools(AirlineService airlineService) {
  //    return MethodToolCallbackProvider.builder().toolObjects(airlineService).build();
  //  }

  public record TextInput(String input) {}

  @Bean
  public ToolCallback toUpperCase() {
    return FunctionToolCallback.builder(
            "toUpperCase", (TextInput input) -> input.input().toUpperCase())
        .inputType(TextInput.class)
        .description("Put the text to upper case")
        .build();
  }
}
