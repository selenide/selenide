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
public abstract class  TemporalFormatCondition<T extends TemporalAccessor> extends Condition {
  private final String originalPattern;
  private final DateTimeFormatter formatter;

  protected TemporalFormatCondition(String name, String pattern) {
    super(name);
    this.originalPattern = pattern;
    this.formatter = DateTimeFormatter.ofPattern(pattern);
  }

  protected abstract TemporalQuery<T> queryFromTemporal();

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String strValue = getValueAttribute(element);
    try {
      return new CheckResult(ACCEPT, formatter.parse(strValue, queryFromTemporal()));
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
  public String format(T value) {
    return formatter.format(value);
  }
}
