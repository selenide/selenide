package com.codeborne.selenide.appium;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.conditions.AttributeWithValue;

public class AppiumCondition {

  private AppiumCondition() {
  }

  public static Condition attributeWithValue(String androidAttributeName, String iosAttributeName, String expectedAttributeValue) {
    return AppiumDriverRunner.isAndroidDriver()
      ? androidAttributeWithValue(androidAttributeName, expectedAttributeValue)
      : iosAttributeWithValue(iosAttributeName, expectedAttributeValue);
  }

  public static Condition androidAttributeWithValue(String androidAttributeName, String expectedAttributeValue) {
    return new AttributeWithValue(androidAttributeName, expectedAttributeValue);
  }

  public static Condition iosAttributeWithValue(String iosAttributeName, String expectedAttributeValue) {
    return new AttributeWithValue(iosAttributeName, expectedAttributeValue);
  }
}
