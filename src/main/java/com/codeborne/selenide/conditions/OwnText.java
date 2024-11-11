package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.commands.GetOwnText.getOwnText;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class OwnText extends CaseInsensitiveTextCondition {
  public OwnText(String expectedText) {
    super("own text", expectedText);
    if (isEmpty(expectedText)) {
      throw new IllegalArgumentException("Argument must not be null or empty string. " +
        "Use $.shouldHave(exactOwnText(\"\").");
    }
  }

  @Override
  protected String getText(Driver driver, WebElement element) {
    return getOwnText(driver, element);
  }
}
