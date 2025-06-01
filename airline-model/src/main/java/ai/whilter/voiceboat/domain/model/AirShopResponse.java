package ai.whilter.voiceboat.domain.model;

import static ai.whilter.voiceboat.domain.CommonConstants.CURRENCY_UNIT_INR;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.javamoney.moneta.Money;

@Getter
@ToString
@AllArgsConstructor(staticName = "of")
@Schema(description = "Available flight and services")
public class AirShopResponse {

  @NotNull
  @Valid
  @Schema(description = "Flight details", requiredMode = Schema.RequiredMode.REQUIRED)
  private Flight flight;

  @NotEmpty
  @Schema(description = "List of Flight Services", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<@NotNull @Valid Service> services;

  @NotNull
  @Schema(
      description = "Flight price",
      //      example = "$470.50",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Money price;

  public static AirShopResponse of(final Flight flight) {
    return new AirShopResponse(flight, List.of(Service.BAG), Money.of(550.75, CURRENCY_UNIT_INR));
  }
}
