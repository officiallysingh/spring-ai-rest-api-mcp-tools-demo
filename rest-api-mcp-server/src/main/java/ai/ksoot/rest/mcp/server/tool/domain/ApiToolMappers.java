package ai.ksoot.rest.mcp.server.tool.domain;

import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolCallback;
import ai.ksoot.rest.mcp.server.tool.domain.model.ApiToolResponse;
import java.util.List;
import java.util.function.Function;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApiToolMappers {

  ApiToolMappers INSTANCE = Mappers.getMapper(ApiToolMappers.class);

  Function<List<ApiToolCallback>, List<ApiToolResponse>> API_TOOLS_LIST_TRANSFORMER =
      revisions -> revisions.stream().map(ApiToolMappers.INSTANCE::toApiToolResponse).toList();

  @Mapping(source = "toolDefinition.name", target = "name")
  @Mapping(source = "toolDefinition.description", target = "description")
  @Mapping(source = "toolDefinition.inputSchema", target = "inputSchema")
  @Mapping(source = "toolMetadata.returnDirect", target = "returnDirect")
  ApiToolResponse toApiToolResponse(final ApiToolCallback apiToolCallback);
}
