package com.codeborne.selenide.appium;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.appium.conditions.AppiumAttributeValues;

import java.util.List;

public class AppiumCollectionCondition {

  private AppiumCollectionCondition() {
  }

  public static CollectionCondition exactAttributes(String androidAttributeName, String iosAttributeName,
                                                    String... expectedAttributeValues) {
    return new AppiumAttributeValues(androidAttributeName, iosAttributeName, expectedAttributeValues);
  }

  public static CollectionCondition exactAttributes(String androidAttributeName, String iosAttributeName,
                                                    List<String> expectedAttributeValues) {
    return new AppiumAttributeValues(androidAttributeName, iosAttributeName, expectedAttributeValues);
  }
}
