package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class TextCondition extends Condition {
  private final String expectedText;
  private String actualText;

  public TextCondition(String name, String expectedText) {
    super(name);
    this.expectedText = expectedText;
  }

  protected abstract boolean match(String actualText, String expectedText);

  protected String getText(WebElement element) {
    return element.getText();
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    actualText = getText(element);
    return match(actualText, expectedText);
  }

  @Nullable
  @Override
  public String actualValue(Driver driver, WebElement element) {
    return actualText;
  }

  @Override
  public String toString() {
    return String.format("%s '%s'", getName(), expectedText);
  }
}
