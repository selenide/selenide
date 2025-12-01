package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

import static java.util.Objects.requireNonNullElseGet;

public abstract class TemporalFormatCondition<T extends TemporalAccessor> extends WebElementCondition {
  private static final Logger log = LoggerFactory.getLogger(TemporalFormatCondition.class);

  private final String pattern;
  private final DateTimeFormatter format;

  protected TemporalFormatCondition(String name, String pattern) {
    super(name);
    this.pattern = pattern;
    this.format = DateTimeFormatter.ofPattern(pattern);
  }

  protected TemporalFormatCondition(String name, DateTimeFormatter format) {
    super(name);
    this.pattern = format.toString();
    this.format = format;
  }

  protected abstract TemporalQuery<T> queryFromTemporal();

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String value = getActualValue(element);
    try {
      return CheckResult.accepted(format.parse(value, queryFromTemporal()));
    } catch (DateTimeParseException exception) {
      log.debug("Unable to parse date string {}: {}", value, exception.toString());
      return CheckResult.rejected(exception.toString(), value);
    }
  }

  @Override
  public String toString() {
    return String.format("%s \"%s\"", getName(), pattern);
  }

  private String getActualValue(WebElement element) {
    String value = element.getAttribute("value");
    return requireNonNullElseGet(value, element::getText);
  }

  public String format(T value) {
    return format.format(value);
  }
}
