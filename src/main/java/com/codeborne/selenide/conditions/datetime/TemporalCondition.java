package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.temporal.TemporalAccessor;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public abstract class TemporalCondition<T extends TemporalAccessor> extends Condition {
  private final TemporalFormatCondition<T> formatCondition;

  protected TemporalCondition(String name, TemporalFormatCondition<T> format) {
    super(name);
    this.formatCondition = format;
  }

  protected abstract boolean matches(T actualDate);

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    CheckResult formatted = formatCondition.check(driver, element);

    return switch (formatted.verdict()) {
      case REJECT -> formatted;
      case ACCEPT -> {
        T localDateValue = requireNonNull(formatted.getActualValue(), "Format condition returns null, not date");
        yield new CheckResult(matches(localDateValue), formatCondition.format(localDateValue));
      }
    };
  }

  protected final String format(T date) {
    return formatCondition.format(date);
  }
}
