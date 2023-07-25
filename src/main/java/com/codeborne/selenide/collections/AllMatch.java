package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class AllMatch extends PredicateCollectionCondition {
  public AllMatch(String description, Predicate<WebElement> predicate) {
    super("all", description, predicate);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    return new CheckResult(
      !elements.isEmpty() && elements.stream().allMatch(predicate),
      elements
    );
  }
}
