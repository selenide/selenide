package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.GetOwnText.getOwnText;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@ParametersAreNonnullByDefault
public class OwnText extends Condition {
  private final String expectedText;

  public OwnText(String expectedText) {
    super("own text");
    this.expectedText = expectedText;
    if (isEmpty(expectedText)) {
      throw new IllegalArgumentException("Argument must not be null or empty string. " +
        "Use $.shouldHave(exactOwnText(\"\").");
    }
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return Html.text.contains(getOwnText(driver, element), expectedText);
  }

  @Override
  public String toString() {
    return String.format("%s '%s'", getName(), expectedText);
  }

  @Nullable
  @Override
  public String actualValue(Driver driver, WebElement element) {
    return getOwnText(driver, element);
  }
}
