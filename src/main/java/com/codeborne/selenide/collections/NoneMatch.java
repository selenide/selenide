package com.codeborne.selenide.collections;

import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class NoneMatch extends PredicateCollectionCondition {
  public NoneMatch(String description, Predicate<WebElement> predicate) {
    super("none", description, predicate);
  }

  @Override
  public boolean test(List<WebElement> elements) {
    if (elements.isEmpty()) {
      return false;
    }
    return elements.stream().noneMatch(predicate);
  }
}
