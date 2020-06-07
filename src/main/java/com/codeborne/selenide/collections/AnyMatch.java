package com.codeborne.selenide.collections;

import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class AnyMatch extends PredicateCollectionCondition {
  public AnyMatch(String description, Predicate<WebElement> predicate) {
    super("any", description, predicate);
  }

  @Override
  public boolean test(List<WebElement> elements) {
    return elements.stream().anyMatch(predicate);
  }
}
