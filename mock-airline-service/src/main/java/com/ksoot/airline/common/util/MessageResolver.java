package com.ksoot.airline.common.util;

/**
 * @author Rajveer Singh
 */
public interface MessageResolver {

  String messageCode();

  default String defaultMessage() {
    return "Message not found for code: '" + this.messageCode() + "'";
  }
}
