// package ai.ksoot.rest.mcp.server.tool.domain.service;
//
// import com.ksoot.airline.domain.model.*;
// import org.springframework.ai.tool.annotation.Tool;
//
//// @Service
// public class AirlineService {
//
//  @Tool(
//      name = "search_flights",
//      description = "Search available flights by origin, destination airports and departure
// date.")
//  public String searchFlights(final AirShopRequest airShopRequest) {
//    return "Flight search results";
//  }
//
//  @Tool(
//      name = "book_flight",
//      description = "Book flight with given flight number and passenger summaries.")
//  String bookFlight(final BookingRequest bookingRequest) {
//    return "Booking created successfully";
//  }
//
//  @Tool(name = "get_flight_booking", description = "Get flight booking by PNR.")
//  String getBooking(final String pnr) {
//    return "Booking details";
//  }
//
//  @Tool(name = "cancel_flight_booking", description = "Cancel flight booking by PNR.")
//  String cancelBooking(final String pnr) {
//    return "Booking cancelled successfully";
//  }
//
//  @Tool(
//      name = "add_service_to_flight_booking",
//      description = "Add service to flight booking for given PNR.")
//  String addServiceToBooking(final String pnr, final AddServiceRequest addServiceRequest) {
//    return "Service added successfully";
//  }
// }
