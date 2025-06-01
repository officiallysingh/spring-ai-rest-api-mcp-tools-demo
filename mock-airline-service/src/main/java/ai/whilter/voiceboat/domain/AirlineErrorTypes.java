package ai.whilter.voiceboat.domain;

import com.ksoot.problem.core.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AirlineErrorTypes implements ErrorType {
  AUDIT_COLLECTION_NOT_FOUND(
      "audit.collection.not.found",
      "Audit collection not found for Source collection: {0}",
      HttpStatus.BAD_REQUEST),
  FLIGHTS_NOT_AVAILABLE(
      "flights.not.available",
      "Flights from: {0} to: {1} for departure date: {2} not available",
      HttpStatus.BAD_REQUEST),
  FLIGHT_NOT_FOUND(
      "flight.not.found", "Flight with Flight number: {0} not found", HttpStatus.NOT_FOUND),
  BOOKING_NOT_FOUND("booking.not.found", "Booking with PNR: {0} not found", HttpStatus.NOT_FOUND),
  CANCELLED_BOOKING_NOT_MODIFIABLE(
      "cancelled.booking.not.modifiable",
      "Can not modify Booking with PNR: {0} as its already cancelled",
      HttpStatus.BAD_REQUEST),
  SERVICE_NOT_ADDED(
      "service.not.added",
      "Could not add service: {0} to Booking with PNR: {1}",
      HttpStatus.INTERNAL_SERVER_ERROR);

  private final String errorKey;

  private final String defaultDetail;

  private final HttpStatus status;

  AirlineErrorTypes(final String errorKey, final String defaultDetail, final HttpStatus status) {
    this.errorKey = errorKey;
    this.defaultDetail = defaultDetail;
    this.status = status;
  }
}
