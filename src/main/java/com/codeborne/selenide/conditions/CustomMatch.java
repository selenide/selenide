package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import java.util.function.Predicate;

public class CustomMatch extends WebElementCondition {
  protected final Predicate<WebElement> predicate;

  public CustomMatch(String description, Predicate<WebElement> predicate) {
    super(description);
    this.predicate = predicate;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean matched = predicate.test(element);
    return new CheckResult(matched, null);
  }


  @Override
  public String toString() {
    return String.format("match '%s' predicate.", getName());
  }
}
