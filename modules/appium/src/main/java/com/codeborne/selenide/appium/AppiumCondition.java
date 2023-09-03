package com.codeborne.selenide.appium;

import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.appium.conditions.AttributeWithValue;
import com.codeborne.selenide.appium.conditions.CombinedAttribute;

public class AppiumCondition {

  public static WebElementCondition attribute(CombinedAttribute attribute, String expectedAttributeValue) {
    return new AttributeWithValue(attribute, expectedAttributeValue);
  }
}
