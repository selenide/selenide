package com.codeborne.selenide;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Pattern;

import static com.codeborne.selenide.SetValueMethod.JS;
import static com.codeborne.selenide.SetValueMethod.SEND_KEYS;
import static java.time.format.DateTimeFormatter.ofPattern;

public class SetValueOptions {
  private static final Pattern REGEX_ANY_CHAR = Pattern.compile(".");
  private static final Formats DATE = new Formats(ofPattern("yyyy-MM-dd"), ofPattern("dd.MM.yyyy"));
  private static final Formats DATETIME = new Formats(ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"), ofPattern("dd.MM.yyyy HH:mm"));
  private static final Formats TIME = new Formats(ofPattern("HH:mm:ss.SSS"), ofPattern("HH:mm"));

  private final CharSequence value;
  private final CharSequence displayedText;
  private final SetValueMethod method;

  protected SetValueOptions(SetValueMethod method, CharSequence value, CharSequence displayedText) {
    this.method = method;
    this.value = value;
    this.displayedText = displayedText;
  }

  /**
   * Text value to set into input field
   */
  public static SetValueOptions withText(CharSequence text) {
    return new SetValueOptions(SEND_KEYS, text, text);
  }

  /**
   * Text value to set into input field of {@code type="date"}
   */
  public static SetValueOptions withDate(LocalDate date) {
    return new SetValueOptions(JS, DATE.value(date), DATE.display(date));
  }

  /**
   * Text value to set into input field of {@code type="datetime-local"}
   */
  public static SetValueOptions withDateTime(LocalDateTime dateTime) {
    return new SetValueOptions(JS, DATETIME.value(dateTime), DATETIME.display(dateTime));
  }

  /**
   * Text value to set into input field of {@code type="time"}
   */
  public static SetValueOptions withTime(LocalTime time) {
    return new SetValueOptions(JS, TIME.value(time), TIME.display(time));
  }

  /**
   * How this value will be displayed in reports.
   * Useful to mask sensitive values like passwords etc.
   */
  public SetValueOptions withDisplayedText(CharSequence displayedText) {
    return new SetValueOptions(method, value, displayedText);
  }

  /**
   * How exactly the value should be set (either "sendKeys" or JavaScript call)
   */
  public SetValueOptions usingMethod(SetValueMethod method) {
    return new SetValueOptions(method, value, displayedText);
  }

  public CharSequence value() {
    return value;
  }

  public SetValueMethod method() {
    return method;
  }

  public SetValueOptions sensitive() {
    return new SetValueOptions(method, value, mask(value));
  }

  private String mask(CharSequence text) {
    return REGEX_ANY_CHAR.matcher(text).replaceAll("*");
  }

  @Override
  public String toString() {
    if (method == SEND_KEYS)
      return displayedText.toString();
    else
      return String.format("\"%s\" (feat. %s)", displayedText, method);
  }

  private record Formats(DateTimeFormatter valueFormat, DateTimeFormatter displayFormat) {
    String value(TemporalAccessor date) {
      return valueFormat.format(date);
    }
    String display(TemporalAccessor date) {
      return displayFormat.format(date);
    }
  }
}
