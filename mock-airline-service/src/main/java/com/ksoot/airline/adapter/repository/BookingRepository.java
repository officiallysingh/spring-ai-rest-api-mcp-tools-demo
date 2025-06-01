package com.ksoot.airline.adapter.repository;

import com.ksoot.airline.domain.model.Booking;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking, String> {

  Optional<Booking> findByPnr(final String pnr);

  boolean existsByPnr(final String pnr);
}
