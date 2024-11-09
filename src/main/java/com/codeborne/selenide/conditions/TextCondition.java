package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.TextCheck;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

public abstract class TextCondition extends WebElementCondition {
  private final String expectedText;

  protected TextCondition(String name, String expectedText) {
    super(name);
    this.expectedText = expectedText;
  }

  protected abstract boolean match(String actualText, String expectedText);

  protected boolean match(TextCheck textCheck, String actualText, String expectedText) {
    return match(actualText, expectedText);
  }

  protected String getText(Driver driver, WebElement element) {
    return element.getText();
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String elementText = getText(driver, element);
    boolean match = match(driver.config().textCheck(), elementText, expectedText);
    return new CheckResult(match, String.format("text=\"%s\"", elementText));
  }

  @Override
  public final String toString() {
    return String.format("%s \"%s\"", getName(), expectedText);
  }
}
