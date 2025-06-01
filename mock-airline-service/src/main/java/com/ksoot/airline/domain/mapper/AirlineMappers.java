package com.ksoot.airline.domain.mapper;

import com.ksoot.airline.domain.model.Booking;
import com.ksoot.airline.domain.model.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Sort;

@Mapper
public interface AirlineMappers {

  AirlineMappers INSTANCE = Mappers.getMapper(AirlineMappers.class);

  Sort SORT_BY_NAME = Sort.by(Sort.Order.asc("name").ignoreCase());

  BookingResponse toBookingResponse(final Booking booking);
}
