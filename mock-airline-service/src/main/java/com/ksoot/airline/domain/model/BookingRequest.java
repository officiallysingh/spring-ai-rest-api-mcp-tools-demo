package com.ksoot.airline.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Schema(description = "Flight booking request")
public class BookingRequest {

  @NotEmpty
  //  @Pattern(regexp = RegularExpressions.REGEX_FLIGHT_NUMBER)
  @Schema(
      description = "Flight number",
      example = "6E7",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String flightNumber;

  @NotEmpty
  @Schema(description = "List of Passenger details", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<@NotNull @Valid Passenger> passengers;

  //  @NotEmpty
  //  @Schema(description = "Services", requiredMode = Schema.RequiredMode.REQUIRED)
  //  private List<@NotEmpty Service> services;
}
