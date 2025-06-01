package ai.ksoot.rest.mcp.server.adapter.repository;

import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolCallback;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApiToolCallbackRepository extends MongoRepository<ApiToolCallback, String> {

  Optional<ApiToolCallback> findByToolDefinitionName(final String toolName);

  boolean existsByToolDefinitionName(final String toolName);

  void deleteByToolDefinitionName(final String toolName);
}
