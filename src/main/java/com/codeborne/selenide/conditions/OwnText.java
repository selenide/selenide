package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.GetOwnText.getOwnText;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@ParametersAreNonnullByDefault
public class OwnText extends TextCondition {
  public OwnText(String expectedText) {
    super("own text", expectedText);
    if (isEmpty(expectedText)) {
      throw new IllegalArgumentException("Argument must not be null or empty string. " +
        "Use $.shouldHave(exactOwnText(\"\").");
    }
  }

  @CheckReturnValue
  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.contains(actualText, expectedText);
  }

  @Nullable
  @CheckReturnValue
  @Override
  protected String getText(Driver driver, WebElement element) {
    return getOwnText(driver, element);
  }
}
