package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class TextCondition extends Condition {
  private final String expectedText;

  protected TextCondition(String name, String expectedText) {
    super(name);
    this.expectedText = expectedText;
  }

  @CheckReturnValue
  protected abstract boolean match(String actualText, String expectedText);

  @Nullable
  @CheckReturnValue
  protected String getText(Driver driver, WebElement element) {
    return element.getText();
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String elementText = getText(driver, element);
    return new CheckResult(match(elementText, expectedText), String.format("text=\"%s\"", elementText));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public final String toString() {
    return String.format("%s \"%s\"", getName(), expectedText);
  }
}
