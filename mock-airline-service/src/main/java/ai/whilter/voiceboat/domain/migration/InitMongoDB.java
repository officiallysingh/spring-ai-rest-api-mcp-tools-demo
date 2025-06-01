package ai.whilter.voiceboat.domain.migration;

import static ai.whilter.voiceboat.common.mongo.MongoSchema.*;

import io.mongock.api.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Slf4j
@ChangeUnit(id = "init-orders-schema", order = "001", author = "rajveer", transactional = false)
public class InitMongoDB {

  @Execution
  public void beforeExecution(final MongoTemplate mongoTemplate) {
    final Index unqIdxPnr = new Index().named("idx_unq_pnr").on("pnr", Sort.Direction.ASC).unique();
    mongoTemplate.indexOps(COLLECTION_BOOKINGS).ensureIndex(unqIdxPnr);
    final Index idxStatus = new Index().named("idx_status").on("status", Sort.Direction.ASC);
    mongoTemplate.indexOps(COLLECTION_BOOKINGS).ensureIndex(idxStatus);
  }

  @RollbackExecution
  public void rollbackBeforeExecution(final MongoTemplate mongoTemplate) {
    // Rollback logic for before execution
    mongoTemplate.dropCollection(COLLECTION_BOOKINGS);
  }
}
