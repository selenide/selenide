package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Objects.requireNonNull;

public class Focused extends WebElementCondition {
  private final ElementDescriber describe = inject(ElementDescriber.class);

  public Focused() {
    super("focused");
  }

  private WebElement getFocusedElement(Driver driver) {
    return requireNonNull(driver.executeJavaScript("return document.activeElement"));
  }

  @Override
  public CheckResult check(Driver driver, WebElement webElement) {
    WebElement focusedElement = getFocusedElement(driver);
    boolean focused = webElement.equals(focusedElement);
    String description = "Focused element: " + describe.briefly(driver, focusedElement) +
                       ", current element: " + describe.briefly(driver, webElement);
    return new CheckResult(focused, description);
  }
}
