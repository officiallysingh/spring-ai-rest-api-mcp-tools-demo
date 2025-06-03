package ai.ksoot.rest.mcp.server.tool.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.util.ParsingUtils;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class ApiToolDefinition implements ToolDefinition {

  private String name;
  private String description;
  private String inputSchema;

  @PersistenceCreator
  public ApiToolDefinition(final String name, final String description, final String inputSchema) {
    this.name = name;
    this.description = description;
    this.inputSchema = inputSchema;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ApiToolDefinition that = (ApiToolDefinition) o;
    return new EqualsBuilder().append(name, that.name).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(name).toHashCode();
  }

  public static DescriptionBuilder named(final String name) {
    return new ApiToolDefinitionBuilder(name);
  }

  public interface DescriptionBuilder {
    InputSchemaBuilder description(final String description);
  }

  public interface InputSchemaBuilder {
    Builder<ApiToolDefinition> inputSchema(final String inputSchema);
  }

  public static final class ApiToolDefinitionBuilder
      implements DescriptionBuilder, InputSchemaBuilder, Builder<ApiToolDefinition> {

    private final String name;

    private String description;

    private String inputSchema;

    private ApiToolDefinitionBuilder(final String name) {
      this.name = name;
    }

    public InputSchemaBuilder description(String description) {
      this.description = description;
      return this;
    }

    public org.apache.commons.lang3.builder.Builder<ApiToolDefinition> inputSchema(
        String inputSchema) {
      this.inputSchema = inputSchema;
      return this;
    }

    public ApiToolDefinition build() {
      if (!StringUtils.hasText(this.description)) {
        Assert.hasText(this.name, "toolName cannot be null or empty");
        this.description = ParsingUtils.reConcatenateCamelCase(this.name, " ");
      }
      return new ApiToolDefinition(this.name, this.description, this.inputSchema);
    }
  }
}
