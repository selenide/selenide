package com.codeborne.selenide.appium;

import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.appium.conditions.AttributeWithValue;
import com.codeborne.selenide.appium.conditions.CombinedAttribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppiumCondition {

  @Nonnull
  @CheckReturnValue
  public static WebElementCondition attribute(CombinedAttribute attribute, String expectedAttributeValue) {
    return new AttributeWithValue(attribute, expectedAttributeValue);
  }
}
