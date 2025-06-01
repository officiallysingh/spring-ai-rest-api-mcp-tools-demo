package ai.whilter.voiceboat.adapter.controller;

import static ai.whilter.voiceboat.common.util.rest.ApiConstants.*;
import static ai.whilter.voiceboat.common.util.rest.ApiStatus.*;

import ai.whilter.voiceboat.common.util.rest.Api;
import ai.whilter.voiceboat.common.util.rest.response.APIResponse;
import ai.whilter.voiceboat.domain.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/airline")
@Tag(name = "Airline", description = "APIs.")
@Validated
public interface AirShopApi extends Api {

  @Operation(
      operationId = "search-flights",
      summary = "Search available flights as per simple search criteria.")
  @ApiResponses(
      value = {@ApiResponse(responseCode = SC_200, description = "Returns available flights")})
  @PostMapping(
      path = "/flights",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<AirShopResponse> searchFlights(
      @Parameter(description = "Search Flight request", required = true) @RequestBody @Valid
          final AirShopRequest airShopRequest);

  @Operation(operationId = "book-flight", summary = "Book a Flight")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = SC_201, description = "Flight booked successfully"),
        @ApiResponse(
            responseCode = SC_400,
            description = "Bad request",
            content = @Content(examples = @ExampleObject(BAD_REQUEST_EXAMPLE_RESPONSE)))
      })
  @PostMapping(
      path = "/bookings",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<APIResponse<?>> bookFlight(
      @Parameter(description = "Book Flight request", required = true) @RequestBody @Valid
          final BookingRequest bookingRequest);

  @Operation(operationId = "get-booking-by-pnr", summary = "Gets a Flight booking by PNR")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = SC_200, description = "Flight Booking returned successfully"),
        @ApiResponse(
            responseCode = SC_404,
            description = "Requested Flight Booking not found",
            content = @Content(examples = @ExampleObject(NOT_FOUND_EXAMPLE_RESPONSE)))
      })
  @GetMapping(path = "/bookings/{pnr}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BookingResponse> getBooking(
      @Parameter(description = "PNR", required = true, example = "BA026")
          @PathVariable(name = "pnr")
          final String pnr);

  @Operation(operationId = "search-bookings", summary = "Search Bookings")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = SC_200, description = "Flight Booking returned successfully"),
        @ApiResponse(
            responseCode = SC_404,
            description = "Requested Flight Bookings not found",
            content = @Content(examples = @ExampleObject(NOT_FOUND_EXAMPLE_RESPONSE)))
      })
  @GetMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<BookingResponse>> getBooking(
      @Parameter(
              description = "Booking status. Allowed values CONFIRMED, CANCELLED, PENDING",
              example = "PENDING")
          @RequestParam(name = "bookingStatus", required = false)
          final BookingStatus bookingStatus);

  @Operation(operationId = "cancel-booking-by-pnr", summary = "Cancels a Flight booking by PNR")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = SC_200, description = "Flight Booking cancelled successfully"),
        @ApiResponse(
            responseCode = SC_404,
            description = "Requested Flight Booking not found",
            content = @Content(examples = @ExampleObject(NOT_FOUND_EXAMPLE_RESPONSE)))
      })
  @PutMapping(path = "/bookings/{pnr}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<APIResponse<?>> cancelBooking(
      @Parameter(description = "PNR", required = true, example = "BA026")
          @PathVariable(name = "pnr")
          final String pnr);

  @Operation(
      operationId = "add-service-to-booking",
      summary = "Add requested Service to Flight Booking")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = SC_200,
            description = "Service added to Flight Booking successfully"),
        @ApiResponse(
            responseCode = SC_404,
            description = "Requested Flight Booking not found",
            content = @Content(examples = @ExampleObject(NOT_FOUND_EXAMPLE_RESPONSE)))
      })
  @PostMapping(
      path = "/bookings/{pnr}/services",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<APIResponse<?>> addServiceToBooking(
      @Parameter(description = "PNR", required = true, example = "BA026")
          @PathVariable(name = "pnr")
          final String pnr,
      @Parameter(description = "Add Service request", required = true, example = "BA026")
          @RequestBody
          @Valid
          final AddServiceRequest addServiceRequest);
}
