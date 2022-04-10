package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.util.regex.Pattern;

import static com.codeborne.selenide.SetValueMethod.JS;
import static com.codeborne.selenide.SetValueMethod.SEND_KEYS;
import static java.time.format.DateTimeFormatter.ofPattern;

@ParametersAreNonnullByDefault
public class SetValueOptions {
  private static final Pattern REGEX_ANY_CHAR = Pattern.compile(".");
  private final CharSequence value;
  private final CharSequence displayedText;
  private final SetValueMethod method;

  private SetValueOptions(SetValueMethod method, CharSequence value, CharSequence displayedText) {
    this.method = method;
    this.value = value;
    this.displayedText = displayedText;
  }

  /**
   * Text value to set into input field
   */
  @CheckReturnValue
  @Nonnull
  public static SetValueOptions withText(CharSequence text) {
    return new SetValueOptions(SEND_KEYS, text, text);
  }

  /**
   * Text value to set into input field
   */
  @CheckReturnValue
  @Nonnull
  public static SetValueOptions withDate(LocalDate date) {
    return new SetValueOptions(JS, date.format(ofPattern("yyyy-MM-dd")), date.format(ofPattern("dd.MM.yyyy")));
  }

  /**
   * How this value will be displayed in reports.
   * Useful to mask sensitive values like passwords etc.
   */
  @CheckReturnValue
  @Nonnull
  public SetValueOptions withDisplayedText(CharSequence displayedText) {
    return new SetValueOptions(method, value, displayedText);
  }

  /**
   * How exactly the value should be set (either "sendKeys" or JavaScript call)
   */
  @CheckReturnValue
  @Nonnull
  public SetValueOptions usingMethod(SetValueMethod method) {
    return new SetValueOptions(method, value, displayedText);
  }

  @CheckReturnValue
  public CharSequence value() {
    return value;
  }

  @CheckReturnValue
  @Nonnull
  public SetValueMethod method() {
    return method;
  }

  @CheckReturnValue
  @Nonnull
  public SetValueOptions sensitive() {
    return new SetValueOptions(method, value, mask(value));
  }

  @Nonnull
  @CheckReturnValue
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
}
