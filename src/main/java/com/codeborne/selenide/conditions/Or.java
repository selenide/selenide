package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Or extends Condition {

  private final List<Condition> conditions;
  private Condition firstFailedCondition;

  public Or(String name, List<Condition> conditions) {
    super(name);
    this.conditions = conditions;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    firstFailedCondition = null;

    for (Condition c : conditions) {
      if (c.apply(driver, element)) {
        return true;
      }
      else if (firstFailedCondition == null) {
        firstFailedCondition = c;
      }
    }
    return false;
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return firstFailedCondition == null ? null : firstFailedCondition.actualValue(driver, element);
  }

  @Override
  public String toString() {
    return firstFailedCondition == null ? super.toString() : firstFailedCondition.toString();
  }
}
