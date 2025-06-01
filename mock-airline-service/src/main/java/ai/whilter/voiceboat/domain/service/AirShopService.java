package ai.whilter.voiceboat.domain.service;

import static ai.whilter.voiceboat.domain.AirlineErrorTypes.*;

import ai.whilter.voiceboat.adapter.repository.BookingRepository;
import ai.whilter.voiceboat.domain.model.Airport;
import ai.whilter.voiceboat.domain.model.Booking;
import ai.whilter.voiceboat.domain.model.BookingRequest;
import ai.whilter.voiceboat.domain.model.BookingStatus;
import ai.whilter.voiceboat.domain.model.Flight;
import ai.whilter.voiceboat.domain.util.FlightNumberGenerator;
import com.ksoot.problem.core.Problems;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AirShopService implements InitializingBean {

  private final BookingRepository bookingRepository;

  private Map<String, Flight> flights;

  public Optional<Flight> searchFlights(
      final Airport origin, final Airport destination, final LocalDate departureDate) {
    final Flight flight =
        this.flights.values().stream()
            .filter(
                flt ->
                    flt.getOrigin().equals(origin)
                        && flt.getDestination().equals(destination)
                        && flt.getDepartureDateTime().toLocalDate().equals(departureDate))
            .findFirst()
            .orElse(null);
    return Optional.ofNullable(flight);
  }

  public Optional<Flight> getFlightByNumber(final String flightNumber) {
    return Optional.ofNullable(this.flights.get(flightNumber));
  }

  @Transactional
  public Booking createBooking(final BookingRequest bookingRequest) {
    final Flight flight =
        getFlightByNumber(bookingRequest.getFlightNumber())
            .orElseThrow(
                () ->
                    Problems.newInstance(FLIGHT_NOT_FOUND)
                        .detailArgs(bookingRequest.getFlightNumber())
                        .throwAble());
    Booking booking = Booking.newInstance(flight, bookingRequest.getPassengers());
    booking = this.bookingRepository.save(booking);
    return booking;
  }

  @Transactional(readOnly = true)
  public Optional<Booking> getBooking(final String pnr) {
    return this.bookingRepository.findByPnr(pnr);
  }

  @Transactional(readOnly = true)
  public List<Booking> getBookings(final BookingStatus bookingStatus) {
    return this.bookingRepository.findAll();
  }

  @Transactional
  public boolean cancelBooking(final String pnr) {
    final Booking booking =
        this.bookingRepository
            .findByPnr(pnr)
            .orElseThrow(() -> Problems.newInstance(BOOKING_NOT_FOUND).detailArgs(pnr).throwAble());
    if (booking.getStatus() == BookingStatus.CANCELLED) {
      return false;
    } else {
      booking.cancel();
      this.bookingRepository.save(booking);
      return true;
    }
  }

  @Transactional
  public boolean addServiceToBooking(
      final String pnr, final ai.whilter.voiceboat.domain.model.Service service) {
    final Booking booking =
        this.bookingRepository
            .findByPnr(pnr)
            .orElseThrow(() -> Problems.newInstance(BOOKING_NOT_FOUND).detailArgs(pnr).throwAble());
    if (booking.getStatus() == BookingStatus.CANCELLED) {
      throw Problems.newInstance(CANCELLED_BOOKING_NOT_MODIFIABLE).detailArgs(pnr).throwAble();
    }
    booking.addService(service);
    this.bookingRepository.save(booking);
    return true;
  }

  @Override
  public void afterPropertiesSet() {
    final LocalDateTime now = LocalDateTime.now();

    String flight1 = FlightNumberGenerator.generateFlightNumber();
    String flight2 = FlightNumberGenerator.generateFlightNumber();
    String flight3 = FlightNumberGenerator.generateFlightNumber();
    String flight4 = FlightNumberGenerator.generateFlightNumber();

    this.flights =
        Map.of(
            flight1,
            Flight.of(
                flight1,
                "Indigo",
                Airport.DEL,
                Airport.JFK,
                now.plusDays(1).truncatedTo(ChronoUnit.MINUTES),
                now.plusDays(2).truncatedTo(ChronoUnit.MINUTES)),
            flight2,
            Flight.of(
                flight2,
                "Indigo",
                Airport.DEL,
                Airport.LAX,
                now.plusDays(2).truncatedTo(ChronoUnit.MINUTES),
                now.plusDays(3).truncatedTo(ChronoUnit.MINUTES)),
            flight3,
            Flight.of(
                flight3,
                "Indigo",
                Airport.BOM,
                Airport.JFK,
                now.plusDays(1).truncatedTo(ChronoUnit.MINUTES),
                now.plusDays(2).truncatedTo(ChronoUnit.MINUTES)),
            flight4,
            Flight.of(
                flight4,
                "Indigo",
                Airport.BOM,
                Airport.LAX,
                now.plusDays(2).truncatedTo(ChronoUnit.MINUTES),
                now.plusDays(3).truncatedTo(ChronoUnit.MINUTES)));
  }
}
