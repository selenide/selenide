package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

@ParametersAreNonnullByDefault
public class LocalDateFormatCondition extends Condition {
  private final String originalPattern;
  private final DateTimeFormatter formatter;

  public LocalDateFormatCondition(String pattern) {
    super("date value format");
    this.originalPattern = pattern;
    this.formatter = DateTimeFormatter.ofPattern(pattern);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String strValue = getValueAttribute(element);
    try {
      return new CheckResult(ACCEPT, formatter.parse(strValue, LocalDate::from));
    } catch (DateTimeParseException exception) {
      return new CheckResult(REJECT, strValue);
    }
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s: \"%s\"", getName(), originalPattern);
  }

  private String getValueAttribute(WebElement element) {
    String attr = element.getAttribute("value");
    return attr == null ? "" : attr;
  }

  @Nonnull
  @CheckReturnValue
  public String formatLocalDate(LocalDate value) {
    return formatter.format(value);
  }
}
