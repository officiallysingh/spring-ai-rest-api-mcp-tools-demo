package ai.whilter.voiceboat.domain.model;

import static ai.whilter.voiceboat.common.CommonConstants.CURRENCY_UNIT_INR;
import static ai.whilter.voiceboat.common.mongo.MongoSchema.COLLECTION_BOOKINGS;

import ai.whilter.voiceboat.common.mongo.AbstractEntity;
import ai.whilter.voiceboat.common.mongo.Auditable;
import ai.whilter.voiceboat.common.util.RegularExpressions;
import ai.whilter.voiceboat.domain.util.PnrGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import lombok.*;
import org.javamoney.moneta.Money;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Auditable
@Document(collection = COLLECTION_BOOKINGS)
@TypeAlias("bookings")
public class Booking extends AbstractEntity {

  @NotEmpty
  @Pattern(regexp = RegularExpressions.REGEX_PNR)
  @Indexed(unique = true)
  private String pnr;

  @NotNull private Flight flight;

  @NotEmpty private List<@NotNull @Valid Passenger> passengers;

  @NotEmpty private List<@NotNull @Valid Service> services;

  @NotNull private Money price;

  @NotNull @Setter @Indexed private BookingStatus status;

  public static Booking newInstance(Flight flight, List<Passenger> passengers) {
    return Booking.builder()
        .pnr(PnrGenerator.generate())
        .flight(flight)
        .passengers(passengers)
        .services(List.of(Service.BAG, Service.MEAL))
        .price(Money.of(550.75, CURRENCY_UNIT_INR))
        .status(BookingStatus.CONFIRMED)
        .build();
  }

  public void cancel() {
    this.status = BookingStatus.CANCELLED;
  }

  public void addService(final Service service) {
    this.services.add(service);
  }
}
