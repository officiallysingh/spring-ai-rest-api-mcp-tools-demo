package com.ksoot.airline.adapter.controller;

import static com.ksoot.airline.domain.AirlineErrorTypes.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ksoot.airline.common.util.rest.response.APIResponse;
import com.ksoot.airline.domain.mapper.AirlineMappers;
import com.ksoot.airline.domain.model.*;
import com.ksoot.airline.domain.service.AirShopService;
import com.ksoot.problem.core.Problems;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class AirShopController implements AirShopApi {

  private final AirShopService airShopService;

  @Override
  public ResponseEntity<AirShopResponse> searchFlights(final AirShopRequest airShopRequest) {
    log.info("Search flight request received: {}", airShopRequest);
    final Flight flight =
        this.airShopService
            .searchFlights(
                airShopRequest.getOrigin(),
                airShopRequest.getDestination(),
                airShopRequest.getDepartureDate())
            .orElseThrow(
                () ->
                    Problems.newInstance(FLIGHTS_NOT_AVAILABLE)
                        .detailArgs(
                            airShopRequest.getOrigin(),
                            airShopRequest.getDestination(),
                            airShopRequest.getDepartureDate())
                        .throwAble());
    log.info("Search flight response returned: {}", flight);
    return ResponseEntity.ok(AirShopResponse.of(flight));
  }

  @Override
  public ResponseEntity<APIResponse<?>> bookFlight(final BookingRequest bookingRequest) {
    log.info("Booking request received: {}", bookingRequest);
    final Booking booking = this.airShopService.createBooking(bookingRequest);
    log.info("Booking created: {}", booking);
    final APIResponse<?> apiResponse =
        APIResponse.of("booking", AirlineMappers.INSTANCE.toBookingResponse(booking))
            .addSuccess("Booking created successfully with PNR: " + booking.getPnr());

    return ResponseEntity.ok()
        .location(
            linkTo(methodOn(AirShopController.class).getBooking(booking.getPnr()))
                .withSelfRel()
                .toUri())
        .body(apiResponse);
  }

  @Override
  public ResponseEntity<BookingResponse> getBooking(final String pnr) {
    log.info("Get booking request received for PNR: {}", pnr);
    final Booking booking =
        this.airShopService
            .getBooking(pnr)
            .orElseThrow(() -> Problems.newInstance(BOOKING_NOT_FOUND).detailArgs(pnr).throwAble());
    log.info("Get booking response returned: {}", booking);
    return ResponseEntity.ok(AirlineMappers.INSTANCE.toBookingResponse(booking));
  }

  @Override
  public ResponseEntity<List<BookingResponse>> getBooking(final BookingStatus bookingStatus) {
    List<BookingResponse> bookings =
        this.airShopService.getBookings(bookingStatus).stream()
            .map(AirlineMappers.INSTANCE::toBookingResponse)
            .toList();
    return ResponseEntity.ok(bookings);
  }

  @Override
  public ResponseEntity<APIResponse<?>> cancelBooking(final String pnr) {
    log.info("Cancel booking request received for PNR: {}", pnr);
    boolean bookingCancelled = this.airShopService.cancelBooking(pnr);
    log.info("Booking cancelled: {}", (bookingCancelled ? "Yes" : "No"));
    return bookingCancelled
        ? ResponseEntity.ok(APIResponse.newInstance().addSuccess("Booking cancelled successfully"))
        : ResponseEntity.ok(APIResponse.newInstance().addWarning("Booking already cancelled"));
  }

  @Override
  public ResponseEntity<APIResponse<?>> addServiceToBooking(
      final String pnr, final AddServiceRequest addServiceRequest) {
    log.info("Add service request received for PNR: {}, Service: {}", pnr, addServiceRequest);
    boolean serviceAdded =
        this.airShopService.addServiceToBooking(pnr, addServiceRequest.getService());
    log.info("Service added: {}", (serviceAdded ? "Yes" : "No"));
    return serviceAdded
        ? ResponseEntity.ok(APIResponse.newInstance().addSuccess("Service added successfully"))
        : ResponseEntity.ok(
            APIResponse.newInstance()
                .addErrors(
                    Problems.newInstance(SERVICE_NOT_ADDED)
                        .detailArgs(addServiceRequest.getService().name(), pnr)));
  }
}
