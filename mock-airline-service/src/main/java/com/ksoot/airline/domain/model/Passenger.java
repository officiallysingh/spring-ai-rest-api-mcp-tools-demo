package com.ksoot.airline.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Valid
@Schema(description = "Passenger details")
public class Passenger {

  @NotEmpty
  @Schema(
      description = "Passenger First name",
      example = "Rajveer",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String firstName;

  @NotEmpty
  @Schema(
      description = "Passenger Last name",
      example = "Singh",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String lastName;

  @NotNull
  @Schema(
      description = "Passenger type, Adult or Child",
      example = "ADULT",
      allowableValues = {"ADULT", "CHILD"},
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Type type;

  public enum Type {
    ADULT,
    CHILD
  }

  public static Passenger adult(String firstName, String lastName) {
    return new Passenger(firstName, lastName, Type.ADULT);
  }

  public static Passenger child(String firstName, String lastName) {
    return new Passenger(firstName, lastName, Type.CHILD);
  }
}
