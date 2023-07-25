package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

@ParametersAreNonnullByDefault
public abstract class TemporalFormatCondition<T extends TemporalAccessor> extends Condition {
  private final String pattern;
  private final DateTimeFormatter format;

  protected TemporalFormatCondition(String name, String pattern) {
    super(name);
    this.pattern = pattern;
    this.format = DateTimeFormatter.ofPattern(pattern);
  }

  @Nonnull
  @CheckReturnValue
  protected abstract TemporalQuery<T> queryFromTemporal();

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String value = getValueAttribute(element);
    try {
      return new CheckResult(ACCEPT, format.parse(value, queryFromTemporal()));
    } catch (DateTimeParseException exception) {
      return new CheckResult(REJECT, value);
    }
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s \"%s\"", getName(), pattern);
  }

  @Nonnull
  @CheckReturnValue
  private String getValueAttribute(WebElement element) {
    String value = element.getAttribute("value");
    return value == null ? "" : value;
  }

  @Nonnull
  @CheckReturnValue
  public String format(T value) {
    return format.format(value);
  }
}
