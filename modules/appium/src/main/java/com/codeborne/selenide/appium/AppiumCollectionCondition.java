package com.codeborne.selenide.appium;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.appium.conditions.AppiumAttributeValues;
import com.codeborne.selenide.appium.conditions.CombinedAttribute;

import java.util.List;

public class AppiumCollectionCondition {

  public static CollectionCondition attributes(CombinedAttribute attribute, String... expectedAttributeValues) {
    return new AppiumAttributeValues(attribute, expectedAttributeValues);
  }

  public static CollectionCondition attributes(CombinedAttribute attribute, List<String> expectedAttributeValues) {
    return new AppiumAttributeValues(attribute, expectedAttributeValues);
  }
}
