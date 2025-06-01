package ai.whilter.voiceboat.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Valid
@Schema(description = "Service details")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Service {
  BAG("1st Bag", "First bag upto 20Kg", "BAG"),
  BAG_ADDITIONAL("2nd Bag", "2nd bag upto 20Kg", "BAG"),
  MEAL("Meal", "Snack", "MEAL"),
  PRIORITY_BOARDING(
      "Priority Boarding", "Priority Boarding, extra charges applicable", "PRIORITY_BOARDING"),
  LOUNGE_ACCESS("Lounge Access", "Lounge Access, extra charges applicable", "LOUNGE_ACCESS");

  @NotEmpty
  @Schema(
      description = "Service name",
      example = "Bag",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private final String name;

  @NotEmpty
  @Schema(
      description = "Service description",
      example = "First bag upto 20Kg",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private final String description;

  @NotNull
  @Schema(
      description = "Service code",
      example = "BAG",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private final String code;

  Service(String name, String description, String code) {
    this.name = name;
    this.description = description;
    this.code = code;
  }
}
