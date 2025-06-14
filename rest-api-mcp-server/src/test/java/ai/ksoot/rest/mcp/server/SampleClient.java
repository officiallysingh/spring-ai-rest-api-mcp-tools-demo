/*
 * Copyright 2024 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.ksoot.rest.mcp.server;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import java.util.Map;

/**
 * @author Christian Tzolov
 */
public class SampleClient {

  private final McpClientTransport transport;

  public SampleClient(McpClientTransport transport) {
    this.transport = transport;
  }

  public void run() {

    var client = McpClient.sync(this.transport).build();

    client.initialize();

    client.ping();

    // List and demonstrate tools
    ListToolsResult toolsList = client.listTools();
    System.out.println("Available Tools = " + toolsList);
    toolsList.tools().stream()
        .forEach(
            tool -> {
              System.out.println(
                  "Tool: "
                      + tool.name()
                      + ", description: "
                      + tool.description()
                      + ", schema: "
                      + tool.inputSchema());
            });

    CallToolResult weatherForcastResult =
        client.callTool(
            new CallToolRequest(
                "getWeatherForecastByLocation",
                Map.of("latitude", "47.6062", "longitude", "-122.3321")));
    System.out.println("Weather Forcast: " + weatherForcastResult);

    CallToolResult alertResult =
        client.callTool(new CallToolRequest("getAlerts", Map.of("state", "NY")));
    System.out.println("Alert Response = " + alertResult);

    client.closeGracefully();
  }
}
