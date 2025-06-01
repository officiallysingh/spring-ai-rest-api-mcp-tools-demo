package com.ksoot.airline.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Valid
@AllArgsConstructor(staticName = "of")
@Schema(description = "Flight details")
public class Flight {

  @NotEmpty
  //  @Pattern(regexp = RegularExpressions.REGEX_FLIGHT_NUMBER)
  @Schema(
      description = "Flight number",
      example = "6E7",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String flightNumber;

  @NotEmpty
  @Schema(
      description = "Airline name",
      example = "Indigo",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String airline;

  @NotNull
  @Schema(
      description = "Departure airport",
      example = "DEL",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Airport origin;

  @NotNull
  @Schema(
      description = "Arrival airport",
      example = "JFK",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Airport destination;

  @NotNull
  @Schema(
      description = "Departure Date time",
      example = "2025-04-21T12:30:00",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDateTime departureDateTime;

  @NotNull
  @Schema(
      description = "Arrival Date",
      example = "2025-04-22T10:00:00",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDateTime arrivalDateTime;
}
