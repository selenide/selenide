package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public class CssClass extends WebElementCondition {
  private final String expectedCssClass;

  public CssClass(String expectedCssClass) {
    super("css class");
    this.expectedCssClass = expectedCssClass;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String actualCssClasses = element.getAttribute("class");
    boolean hasClass = actualCssClasses != null && contains(actualCssClasses.split(" "), expectedCssClass);
    return new CheckResult(hasClass, String.format("class=\"%s\"", actualCssClasses));
  }

  @Override
  public String toString() {
    return String.format("%s \"%s\"", getName(), expectedCssClass);
  }

  private <T> boolean contains(T[] objects, T object) {
    for (T object1 : objects) {
      if (object.equals(object1)) {
        return true;
      }
    }
    return false;
  }
}
