package ai.whilter.voiceboat.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Booking Status")
public enum BookingStatus {
  CONFIRMED,
  CANCELLED,
  PENDING
}
