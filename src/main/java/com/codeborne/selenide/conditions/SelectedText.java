package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SelectedText extends Condition {
  private final String expectedText;
  private final ThreadLocal<String> actualResult = ThreadLocal.withInitial(() -> "");

  public SelectedText(String expectedText) {
    super("selectedText");
    this.expectedText = expectedText;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    String selectedText = driver.executeJavaScript(
      "return arguments[0].value.substring(arguments[0].selectionStart, arguments[0].selectionEnd);", element);
    actualResult.set(selectedText);
    return actualResult.get().equals(expectedText);
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return "'" + actualResult.get() + "'";
  }

  @Override
  public String toString() {
    return String.format("%s '%s'", getName(), expectedText);
  }
}
