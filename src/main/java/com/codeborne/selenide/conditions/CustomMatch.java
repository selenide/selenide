package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class CustomMatch extends Condition {
  protected final Predicate<WebElement> predicate;

  public CustomMatch(String description, Predicate<WebElement> predicate) {
    super(description);
    this.predicate = predicate;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean matched = predicate.test(element);
    return new CheckResult(matched, null);
  }


  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("match '%s' predicate.", getName());
  }
}
