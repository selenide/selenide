package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.function.Predicate;

public class CustomMatch extends Condition {
  protected final Predicate<WebElement> predicate;

  public CustomMatch(String description, final Predicate<WebElement> predicate) {
    super(description);
    this.predicate = predicate;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return predicate.test(element);
  }


  @Override
  public String toString() {
    return String.format("match '%s' predicate but did not.", getName());
  }

}
