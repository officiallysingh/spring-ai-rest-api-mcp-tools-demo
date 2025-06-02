package ai.ksoot.rest.mcp.server.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;

public class HttpHeadersSerializer extends JsonSerializer<HttpHeaders> {

  @Override
  public void serialize(HttpHeaders headers, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartObject();

    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      gen.writeArrayFieldStart(entry.getKey());

      for (String value : entry.getValue()) {
        // For headers like "Accept", split on commas
        if (entry.getKey().equalsIgnoreCase(HttpHeaders.ACCEPT)) {
          for (String mediaType : value.split(",")) {
            gen.writeString(mediaType.trim());
          }
        } else {
          gen.writeString(value);
        }
      }

      gen.writeEndArray();
    }

    gen.writeEndObject();
  }
}
