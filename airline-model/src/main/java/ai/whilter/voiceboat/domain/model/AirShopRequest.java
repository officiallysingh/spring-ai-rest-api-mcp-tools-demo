package ai.whilter.voiceboat.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Valid
@Schema(description = "Airshop request to search flights")
public class AirShopRequest {

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
      description = "Departure Date",
      example = "2025-04-21",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDate departureDate;

  //  @NotEmpty
  //  @Schema(
  //      description = "List of Passenger summaries", requiredMode = Schema.RequiredMode.REQUIRED
  ////     , example = "[{\"count\":2,\"type\":\"ADULT\"}, {\"count\":1,\"type\":\"CHILD\"}]"
  //  )
  //  private List<@NotNull @Valid Pax> passengers;
}
