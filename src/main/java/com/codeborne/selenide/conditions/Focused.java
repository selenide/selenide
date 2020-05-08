package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Describe;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Focused extends Condition {

  public Focused() {
    super("focused");
  }

  private WebElement getFocusedElement(Driver driver) {
    return (WebElement) driver.executeJavaScript("return document.activeElement");
  }

  @Override
  public boolean apply(Driver driver, WebElement webElement) {
    WebElement focusedElement = getFocusedElement(driver);
    return focusedElement != null && focusedElement.equals(webElement);
  }

  @Override
  public String actualValue(Driver driver, WebElement webElement) {
    WebElement focusedElement = getFocusedElement(driver);
    return focusedElement == null ? "No focused focusedElement found " :
      "Focused focusedElement: " + Describe.describe(driver, focusedElement) +
        ", current focusedElement: " + Describe.describe(driver, webElement);
  }
}
