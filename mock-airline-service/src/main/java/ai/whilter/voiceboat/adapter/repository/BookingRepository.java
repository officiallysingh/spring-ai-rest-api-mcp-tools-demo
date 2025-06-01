package ai.whilter.voiceboat.adapter.repository;

import ai.whilter.voiceboat.domain.model.Booking;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking, String> {

  Optional<Booking> findByPnr(final String pnr);

  boolean existsByPnr(final String pnr);
}
