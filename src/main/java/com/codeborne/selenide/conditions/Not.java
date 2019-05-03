package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

public class Not extends Condition {
  private final Condition condition;

  public Not(Condition originalCondition) {
    super("not " + originalCondition.getName(), !originalCondition.missingElementSatisfiesCondition());
    this.condition = originalCondition;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return !condition.apply(driver, element);
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return condition.actualValue(driver, element);
  }
}
