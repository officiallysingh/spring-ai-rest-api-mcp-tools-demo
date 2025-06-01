package com.ksoot.airline.domain.model;

import com.ksoot.airline.domain.RegularExpressions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.*;
import org.javamoney.moneta.Money;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Flight booking details")
public class BookingResponse {

  @NotEmpty
  @Schema(
      description = "Flight booking Order Id",
      example = "659fa62f5f43f16e1e011223",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;

  @NotEmpty
  @Pattern(regexp = RegularExpressions.REGEX_PNR)
  @Schema(
      description = "Flight booking PNR",
      example = "BA026",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String pnr;

  @NotNull
  @Schema(description = "Flight details", requiredMode = Schema.RequiredMode.REQUIRED)
  private Flight flight;

  @NotEmpty
  @Schema(description = "List of Passenger details", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<@NotNull @Valid Passenger> passengers;

  @NotEmpty
  @Schema(description = "List of Flight Services", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<@NotEmpty Service> services;

  @Schema(
      description = "Flight Booking status.",
      allowableValues = {"CONFIRMED", "CANCELLED", "PENDING"},
      example = "CONFIRMED",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull
  @Setter
  private BookingStatus status;

  @Schema(description = "Flight Booking price", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull
  private Money price;
}
