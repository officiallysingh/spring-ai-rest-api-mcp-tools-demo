package com.ksoot.airline.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Airport details")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Airport {
  // USA Airports
  JFK("JFK", "John F. Kennedy International Airport", "New York", "USA"),
  LAX("LAX", "Los Angeles International Airport", "Los Angeles", "USA"),
  //  ATL("ATL", "Hartsfield-Jackson Atlanta International Airport", "Atlanta", "USA"),
  //  DFW("DFW", "Dallas/Fort Worth International Airport", "Dallas", "USA"),
  //  DEN("DEN", "Denver International Airport", "Denver", "USA"),

  // Indian Airports
  DEL("DEL", "Indira Gandhi International Airport", "Delhi", "India"),
  BOM("BOM", "Chhatrapati Shivaji Maharaj International Airport", "Mumbai", "India");
  //  BLR("BLR", "Kempegowda International Airport", "Bengaluru", "India"),
  //  HYD("HYD", "Rajiv Gandhi International Airport", "Hyderabad", "India"),
  //  MAA("MAA", "Chennai International Airport", "Chennai", "India");

  private final String iataCode;

  private final String name;

  private final String city;

  private final String country;

  Airport(final String iataCode, final String name, final String city, final String country) {
    this.iataCode = iataCode;
    this.name = name;
    this.city = city;
    this.country = country;
  }
}
