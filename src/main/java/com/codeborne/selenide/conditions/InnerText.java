package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.commands.GetInnerText.getInnerText;

public class InnerText extends CaseInsensitiveTextCondition {

  public InnerText(String expectedText) {
    super("inner text", expectedText);
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return getInnerText(driver, element);
  }
}
