package ai.ksoot.rest.mcp.server.tool.domain.model;

public class ApiSchema {

  public static final String SEARCH_FLIGHTS_SCHEMA =
      """
      "{\\n  \\"$schema\\" : \\"https://json-schema.org/draft/2020-12/schema\\",\\n  \\"type\\" : \\"object\\",\\n  \\"properties\\" : {\\n    \\"airShopRequest\\" : {\\n      \\"type\\" : \\"object\\",\\n      \\"description\\" : \\"Search Flight request\\",\\n      \\"properties\\" : {\\n        \\"origin\\" : {\\n          \\"type\\" : \\"string\\",\\n          \\"description\\" : \\"Departure airport\\",\\n          \\"enum\\" : [ \\"JFK\\", \\"LAX\\", \\"DEL\\", \\"BOM\\" ],\\n          \\"example\\" : \\"DEL\\"\\n        },\\n        \\"destination\\" : {\\n          \\"type\\" : \\"string\\",\\n          \\"description\\" : \\"Arrival airport\\",\\n          \\"enum\\" : [ \\"JFK\\", \\"LAX\\", \\"DEL\\", \\"BOM\\" ],\\n          \\"example\\" : \\"JFK\\"\\n        },\\n        \\"departureDate\\" : {\\n          \\"type\\" : \\"string\\",\\n          \\"format\\" : \\"date\\",\\n          \\"description\\" : \\"Departure Date\\",\\n          \\"example\\" : \\"2025-04-21\\"\\n        }\\n      },\\n      \\"required\\" : [ \\"departureDate\\", \\"destination\\", \\"origin\\" ]\\n    }\\n  },\\n  \\"required\\" : [ \\"airShopRequest\\" ],\\n  \\"additionalProperties\\" : false\\n}"
      """;

  public static final String BOOK_FLIGHT_SCHEMA =
      """
      "{\\n  \\"$schema\\" : \\"https://json-schema.org/draft/2020-12/schema\\",\\n  \\"type\\" : \\"object\\",\\n  \\"properties\\" : {\\n    \\"bookingRequest\\" : {\\n      \\"type\\" : \\"object\\",\\n      \\"description\\" : \\"Book Flight request\\",\\n      \\"properties\\" : {\\n        \\"flightNumber\\" : {\\n          \\"type\\" : \\"string\\",\\n          \\"description\\" : \\"Flight number\\",\\n          \\"example\\" : 60000000\\n        },\\n        \\"passengers\\" : {\\n          \\"type\\" : \\"array\\",\\n          \\"items\\" : {\\n            \\"type\\" : \\"object\\",\\n            \\"description\\" : \\"Passenger details\\",\\n            \\"properties\\" : {\\n              \\"firstName\\" : {\\n                \\"type\\" : \\"string\\",\\n                \\"description\\" : \\"Passenger First name\\",\\n                \\"example\\" : \\"Rajveer\\"\\n              },\\n              \\"lastName\\" : {\\n                \\"type\\" : \\"string\\",\\n                \\"description\\" : \\"Passenger Last name\\",\\n                \\"example\\" : \\"Singh\\"\\n              },\\n              \\"type\\" : {\\n                \\"type\\" : \\"string\\",\\n                \\"description\\" : \\"Passenger type, Adult or Child\\",\\n                \\"enum\\" : [ \\"ADULT\\", \\"CHILD\\" ],\\n                \\"example\\" : \\"ADULT\\"\\n              }\\n            },\\n            \\"required\\" : [ \\"firstName\\", \\"lastName\\", \\"type\\" ]\\n          },\\n          \\"description\\" : \\"List of Passenger details\\"\\n        }\\n      },\\n      \\"required\\" : [ \\"flightNumber\\", \\"passengers\\" ]\\n    }\\n  },\\n  \\"required\\" : [ \\"bookingRequest\\" ],\\n  \\"additionalProperties\\" : false\\n}"
      """;

  public static final String GET_BOOKING_SCHEMA =
      """
      "{\\n  \\"$schema\\" : \\"https://json-schema.org/draft/2020-12/schema\\",\\n  \\"type\\" : \\"object\\",\\n  \\"properties\\" : {\\n    \\"pnr\\" : {\\n      \\"type\\" : \\"string\\"\\n    }\\n  },\\n  \\"required\\" : [ \\"pnr\\" ],\\n  \\"additionalProperties\\" : false\\n}"
      """;

  public static final String CANCEL_BOOKING_SCHEMA =
      """
      "{\\n  \\"$schema\\" : \\"https://json-schema.org/draft/2020-12/schema\\",\\n  \\"type\\" : \\"object\\",\\n  \\"properties\\" : {\\n    \\"pnr\\" : {\\n      \\"type\\" : \\"string\\"\\n    }\\n  },\\n  \\"required\\" : [ \\"pnr\\" ],\\n  \\"additionalProperties\\" : false\\n}"
      """;
}
