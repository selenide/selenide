package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Predicate;

public class AnyMatch extends PredicateCollectionCondition {
  public AnyMatch(String description, Predicate<WebElement> predicate) {
    super("any of", description, predicate);
  }

  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    return new CheckResult(elements.stream().anyMatch(predicate), elements);
  }
}
