package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

public abstract class TemporalFormatCondition<T extends TemporalAccessor> extends WebElementCondition {
  private final String pattern;
  private final DateTimeFormatter format;

  protected TemporalFormatCondition(String name, String pattern) {
    super(name);
    this.pattern = pattern;
    this.format = DateTimeFormatter.ofPattern(pattern);
  }

  protected abstract TemporalQuery<T> queryFromTemporal();

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String value = getValueAttribute(element);
    try {
      return new CheckResult(ACCEPT, format.parse(value, queryFromTemporal()));
    } catch (DateTimeParseException exception) {
      return new CheckResult(REJECT, value);
    }
  }

  @Override
  public String toString() {
    return String.format("%s \"%s\"", getName(), pattern);
  }

  private String getValueAttribute(WebElement element) {
    String value = element.getAttribute("value");
    return value == null ? "" : value;
  }

  public String format(T value) {
    return format.format(value);
  }
}
