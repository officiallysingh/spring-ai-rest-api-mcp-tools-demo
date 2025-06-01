package ai.ksoot.rest.mcp.server.tool.domain;

import static ai.ksoot.rest.mcp.server.common.mongo.MongoSchema.COLLECTION_TOOLS;

import io.mongock.api.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Slf4j
@ChangeUnit(id = "init-tools-schema", order = "001", author = "rajveer", transactional = false)
public class InitMongoDB {

  @Execution
  public void beforeExecution(final MongoTemplate mongoTemplate) {
    final Index unqIdxToolName =
        new Index()
            .named("idx_unq_tool_name")
            .on("tool_definition.name", Sort.Direction.ASC)
            .unique();
    mongoTemplate.indexOps(COLLECTION_TOOLS).ensureIndex(unqIdxToolName);
  }

  @RollbackExecution
  public void rollbackBeforeExecution(final MongoTemplate mongoTemplate) {
    // Rollback logic for before execution
    mongoTemplate.dropCollection(COLLECTION_TOOLS);
  }
}
