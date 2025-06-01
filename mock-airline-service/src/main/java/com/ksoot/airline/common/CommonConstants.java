package com.ksoot.airline.common;

import java.util.Locale;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CommonConstants {

  public static final String PATH_VAR_ID = "{id}";

  public static final String MEDIA_TYPE_ANY = "*/*";

  public static final String API_CONTEXT = "/**";

  public static final String SYSTEM_USER = "SYSTEM";

  public static final String HEADER_EXPAND = "x-expand";

  public static final String HEADER_LOCATION = "Location";

  public static final String HEADER_AUTHORIZATION = "Authorization";

  public static final String DOT = ".";

  public static final String SLASH = "/";

  public static final Locale SYSTEM_LOCALE = Locale.getDefault();

  public static final String API = SLASH + "api";

  public static final String V1 = SLASH + "v1";

  public static final String V2 = SLASH + "v2";

  public static final int DEFAULT_PAGE_SIZE = 16;

  public static final CurrencyUnit CURRENCY_UNIT_INR = Monetary.getCurrency("INR");

  public static final CurrencyUnit CURRENCY_UNIT_USD = Monetary.getCurrency("USD");
}
