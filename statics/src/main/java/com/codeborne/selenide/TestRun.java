package com.codeborne.selenide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestRun {
  private static final Logger log = LoggerFactory.getLogger(TestRun.class);

  private TestRun() {
    throw new IllegalStateException("Util class");
  }

  /**
   * Since a test run is unique for all tests in a run, we only need to set this value once and as static - final.
   */
  public static String getUniqueTestRunName() {
    //
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy-HH-mm-ss");
    return getUniqueTestRunName(dateTimeFormatter);
  }

  /**
   * Convenience method to allow for a different test run formatting then the one provided by default.
   */
  public static String getUniqueTestRunName(DateTimeFormatter dateTimeFormatter) {
    String testRun = getFormattedDate(dateTimeFormatter);

    log.info("Test run name: {}", testRun);
    return testRun;
  }

  public static String getFormattedDate(DateTimeFormatter dateTimeFormatter) {
    LocalDateTime localDateTime = LocalDateTime.now();
    log.debug("Before formatting: {}", localDateTime);

    return localDateTime.format(dateTimeFormatter);
  }
}
