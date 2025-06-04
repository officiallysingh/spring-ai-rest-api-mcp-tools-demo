package ai.ksoot.rest.mcp.server.adapter.repository;

import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolCallback;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApiToolCallbackRepository extends MongoRepository<ApiToolCallback, String> {

  Optional<ApiToolCallback> findByToolDefinitionName(final String name);

  boolean existsByToolDefinitionName(final String name);

  void deleteByToolDefinitionName(final String name);
}
