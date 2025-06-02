package ai.ksoot.rest.mcp.server.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.springframework.http.HttpHeaders;

public class HttpHeadersDeserializer extends JsonDeserializer<HttpHeaders> {

  @Override
  public HttpHeaders deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException {
    JsonNode node = parser.getCodec().readTree(parser);
    HttpHeaders headers = new HttpHeaders();

    Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      String key = entry.getKey();
      JsonNode valueNode = entry.getValue();

      if (valueNode.isArray()) {
        for (JsonNode val : valueNode) {
          headers.add(key, val.asText());
        }
      } else {
        headers.add(key, valueNode.asText());
      }
    }

    return headers;
  }
}
