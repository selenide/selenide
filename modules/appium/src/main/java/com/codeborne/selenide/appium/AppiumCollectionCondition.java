package com.codeborne.selenide.appium;

import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.appium.conditions.AppiumAttributeValues;
import com.codeborne.selenide.appium.conditions.CombinedAttribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class AppiumCollectionCondition {

  @Nonnull
  @CheckReturnValue
  public static WebElementsCondition attributes(CombinedAttribute attribute, String... expectedAttributeValues) {
    return new AppiumAttributeValues(attribute, expectedAttributeValues);
  }

  @Nonnull
  @CheckReturnValue
  public static WebElementsCondition attributes(CombinedAttribute attribute, List<String> expectedAttributeValues) {
    return new AppiumAttributeValues(attribute, expectedAttributeValues);
  }
}
