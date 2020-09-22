package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class Focused extends Condition {
  private final ElementDescriber describe = inject(ElementDescriber.class);

  public Focused() {
    super("focused");
  }

  private WebElement getFocusedElement(Driver driver) {
    return driver.executeJavaScript("return document.activeElement");
  }

  @Override
  public boolean apply(Driver driver, WebElement webElement) {
    WebElement focusedElement = getFocusedElement(driver);
    return focusedElement != null && focusedElement.equals(webElement);
  }

  @Override
  public String actualValue(Driver driver, WebElement webElement) {
    WebElement focusedElement = getFocusedElement(driver);
    return focusedElement == null ? "No focused element found " :
      "Focused element: " + describe.fully(driver, focusedElement) +
        ", current element: " + describe.fully(driver, webElement);
  }
}
