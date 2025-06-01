package com.ksoot.airline.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Valid
@Schema(description = "Add service request")
public class AddServiceRequest {

  @NotNull
  @Schema(
      description = "Service",
      allowableValues = {"BAG", "BAG_ADDITIONAL", "MEAL", "PRIORITY_BOARDING", "LOUNGE_ACCESS"},
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Service service;
}
