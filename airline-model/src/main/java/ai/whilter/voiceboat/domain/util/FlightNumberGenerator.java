package ai.whilter.voiceboat.domain.util;

import java.util.Random;

public class FlightNumberGenerator {

  private static final String INDIGO_IATA_CODE = "6E";
  private static final Random random = new Random();

  public static String generateFlightNumber() {
    // Generate a random number with 1 to 4 digits
    int digitCount = random.nextInt(4) + 1; // 1 to 4
    int max = (int) Math.pow(10, digitCount) - 1;
    int min = (int) Math.pow(10, digitCount - 1);
    int flightNumPart = random.nextInt(max - min + 1) + min;

    // Optionally add a suffix letter (A-Z)
    boolean addSuffix = random.nextBoolean(); // 50% chance to add suffix
    char suffix = (char) ('A' + random.nextInt(26));

    return INDIGO_IATA_CODE + flightNumPart + (addSuffix ? suffix : "");
  }
}
