package com.codeborne.selenide.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Predicate;

public class AllMatch extends PredicateCollectionCondition {
  public AllMatch(String description, Predicate<WebElement> predicate) {
    super("all", description, predicate);
  }

  @Override
  public CheckResult check(Driver driver, List<WebElement> elements) {
    return new CheckResult(
      !elements.isEmpty() && elements.stream().allMatch(predicate),
      elements
    );
  }
}
