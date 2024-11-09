package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Predicate;

public class NoneMatch extends PredicateCollectionCondition {
  public NoneMatch(String description, Predicate<WebElement> predicate) {
    super("none of", description, predicate);
  }

  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    return new CheckResult(
      !elements.isEmpty() && elements.stream().noneMatch(predicate),
      elements
    );
  }
}
