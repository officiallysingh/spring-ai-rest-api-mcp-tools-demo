package ai.whilter.voiceboat.domain.util;

import java.security.SecureRandom;

public class PnrGenerator {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();

  public static String generate() {
    int length = RANDOM.nextBoolean() ? 5 : 6; // randomly choose 5 or 6 characters
    StringBuilder pnr = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int index = RANDOM.nextInt(CHARACTERS.length());
      pnr.append(CHARACTERS.charAt(index));
    }
    return pnr.toString();
  }
}
