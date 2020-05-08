package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExactText extends Condition {
  private final String expectedText;

  public ExactText(String expectedText) {
    super("exact text");
    this.expectedText = expectedText;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return Html.text.equals(element.getText(), expectedText);
  }

  @Override
  public String toString() {
    return String.format("%s '%s'", getName(), expectedText);
  }

}
