package com.codeborne.selenide.appium;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.appium.conditions.AttributeWithValue;
import com.codeborne.selenide.appium.conditions.CombinedAttribute;

public class AppiumCondition {

  public static Condition attribute(CombinedAttribute attribute, String expectedAttributeValue) {
    return new AttributeWithValue(attribute, expectedAttributeValue);
  }
}
