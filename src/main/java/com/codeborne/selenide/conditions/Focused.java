package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ElementDescriber;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
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

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement webElement) {
    WebElement focusedElement = getFocusedElement(driver);
    boolean focused = focusedElement != null && focusedElement.equals(webElement);
    String description = focusedElement == null ? "No focused element found " :
      "Focused element: " + describe.briefly(driver, focusedElement) +
        ", current element: " + describe.briefly(driver, webElement);
    return new CheckResult(focused, description);
  }
}
