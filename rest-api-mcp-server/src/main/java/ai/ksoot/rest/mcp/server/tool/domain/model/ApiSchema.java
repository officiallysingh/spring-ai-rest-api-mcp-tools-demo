package ai.ksoot.rest.mcp.server.tool.domain.model;

public class ApiSchema {

  public static final String SEARCH_FLIGHTS_SCHEMA =
      """
        "{
          "$schema" : "https://json-schema.org/draft/2020-12/schema",
          "type" : "object",
          "properties" : {
            "airShopRequest" : {
              "type" : "object",
              "description" : "Search Flight request",
              "properties" : {
                "origin" : {
                  "type" : "string",
                  "description" : "Departure airport",
                  "enum" : [ "JFK", "LAX", "DEL", "BOM" ],
                  "example" : "DEL"
                },
                "destination" : {
                  "type" : "string",
                  "description" : "Arrival airport",
                  "enum" : [ "JFK", "LAX", "DEL", "BOM" ],
                  "example" : "JFK"
                },
                "departureDate" : {
                  "type" : "string",
                  "format" : "date",
                  "description" : "Departure Date",
                  "example" : "2025-04-21"
                }
              },
              "required" : [ "departureDate", "destination", "origin" ]
            }
          },
          "required" : [ "airShopRequest" ],
          "additionalProperties" : false
        }"
        """;

  public static final String BOOK_FLIGHT_SCHEMA =
      """
      {
        "$schema" : "https://json-schema.org/draft/2020-12/schema",
        "type" : "object",
        "properties" : {
          "bookingRequest" : {
            "type" : "object",
            "properties" : {
              "flightNumber" : {
                "type" : "string",
                "description" : "Flight number"
              },
              "passengers" : {
                "description" : "List of Passenger details",
                "type" : "array",
                "items" : {
                  "type" : "object",
                  "properties" : {
                    "firstName" : {
                      "type" : "string",
                      "description" : "Passenger First name"
                    },
                    "lastName" : {
                      "type" : "string",
                      "description" : "Passenger Last name"
                    },
                    "type" : {
                      "type" : "string",
                      "enum" : [ "ADULT", "CHILD" ],
                      "description" : "Passenger type, Adult or Child"
                    }
                  },
                  "required" : [ "firstName", "lastName", "type" ],
                  "description" : "Passenger details"
                }
              }
            },
            "required" : [ "flightNumber", "passengers" ],
            "description" : "Flight booking request"
          }
        },
        "required" : [ "bookingRequest" ],
        "additionalProperties" : false
      }
      """;

  public static final String GET_BOOKING_SCHEMA =
      """
      {
         "$schema" : "https://json-schema.org/draft/2020-12/schema",
         "type" : "object",
         "properties" : {
           "pnr" : {
             "type" : "string"
           }
         },
         "required" : [ "pnr" ],
         "additionalProperties" : false
       }
      """;

  public static final String CANCEL_BOOKING_SCHEMA =
      """
      {
        "$schema" : "https://json-schema.org/draft/2020-12/schema",
        "type" : "object",
        "properties" : {
          "pnr" : {
            "type" : "string"
          }
        },
        "required" : [ "pnr" ],
        "additionalProperties" : false
      }
      """;
}
