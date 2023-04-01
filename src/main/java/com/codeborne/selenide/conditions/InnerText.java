package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.GetInnerText.getInnerText;

@ParametersAreNonnullByDefault
public class InnerText extends CaseInsensitiveTextCondition {

  public InnerText(String expectedText) {
    super("inner text", expectedText);
  }

  @Nullable
  @CheckReturnValue
  @Override
  protected String getText(Driver driver, WebElement element) {
    return getInnerText(driver, element);
  }
}
