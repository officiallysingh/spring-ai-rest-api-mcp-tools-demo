package ai.whilter.voiceboat.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Valid
@Schema(description = "Passenger summary")
public record Pax(
    @Min(1)
        @Max(4)
        @NotNull
        @Schema(description = "No of Passengers of a specific type", example = "2")
        Short count,
    @NotNull @Schema(description = "Passenger type, Adult or Child", example = "ADULT")
        Passenger.Type type) {}
