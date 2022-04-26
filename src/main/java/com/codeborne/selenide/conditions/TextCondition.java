package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.TextCheck;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.util.stream.Collectors.joining;

@ParametersAreNonnullByDefault
public abstract class TextCondition extends Condition {
  private final String expectedText;

  protected TextCondition(String name, String expectedText) {
    super(name);
    this.expectedText = expectedText;
  }

  @CheckReturnValue
  protected abstract boolean match(String actualText, String expectedText);
  protected boolean match(TextCheck textCheck, String actualText, String expectedText) {
    return match(actualText, expectedText);
  }

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
    boolean match = match(driver.config().textCheck(), elementText, expectedText);
    return new CheckResult(match, String.format("text=\"%s\"", elementText));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public final String toString() {
    return String.format("%s \"%s\"", getName(), expectedText);
  }

  protected String getSelectedOptionsTexts(WebElement element) {
    List<WebElement> selectedOptions = new Select(element).getAllSelectedOptions();
    return selectedOptions.stream().map(WebElement::getText).collect(joining());
  }
}
