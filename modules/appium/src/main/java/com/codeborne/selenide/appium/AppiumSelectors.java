package com.codeborne.selenide.appium;

import com.codeborne.selenide.appium.selector.ByClassNameAndIndex;
import com.codeborne.selenide.appium.selector.ByTagAndAttribute;
import com.codeborne.selenide.appium.selector.ByTagAndText;
import com.codeborne.selenide.appium.selector.ByText;
import com.codeborne.selenide.appium.selector.WithTagAndText;
import com.codeborne.selenide.appium.selector.WithText;
import com.codeborne.selenide.appium.selector.ByTagAndName;
import com.codeborne.selenide.appium.selector.ByName;
import com.codeborne.selenide.appium.selector.WithName;
import com.codeborne.selenide.appium.selector.WithTagAndName;
import com.codeborne.selenide.appium.selector.ByAttribute;
import com.codeborne.selenide.appium.selector.WithTagAndAttribute;
import com.codeborne.selenide.appium.selector.WithAttribute;
import com.codeborne.selenide.appium.selector.ByTagAndContentDescription;
import com.codeborne.selenide.appium.selector.WithTagAndContentDescription;
import com.codeborne.selenide.appium.selector.ByContentDescription;
import com.codeborne.selenide.appium.selector.WithContentDescription;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public class AppiumSelectors {

  private AppiumSelectors() {
  }

  @CheckReturnValue
  @Nonnull
  public static By byTagAndText(String tag, String elementText) {
    return new ByTagAndText(tag, elementText);
  }

  @CheckReturnValue
  @Nonnull
  public static By byText(String elementText) {
    return new ByText(elementText);
  }

  @CheckReturnValue
  @Nonnull
  public static By withTagAndText(String tag, String elementText) {
    return new WithTagAndText(tag, elementText);
  }

  @CheckReturnValue
  @Nonnull
  public static By withText(String elementText) {
    return new WithText(elementText);
  }

  @CheckReturnValue
  @Nonnull
  public static By byTagAndName(String tag, String nameAttributeValue) {
    return new ByTagAndName(tag, nameAttributeValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By byName(String nameAttributeValue) {
    return new ByName(nameAttributeValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By withTagAndName(String tag, String nameAttributeValue) {
    return new WithTagAndName(tag, nameAttributeValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By withName(String nameAttributeValue) {
    return new WithName(nameAttributeValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By byTagAndAttribute(String tag, String attributeName, String attributeValue) {
    return new ByTagAndAttribute(tag, attributeName, attributeValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By byAttribute(String attributeName, String attributeValue) {
    return new ByAttribute(attributeName, attributeValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By withTagAndAttribute(String tag, String attributeName, String attributeValue) {
    return new WithTagAndAttribute(tag, attributeName, attributeValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By withAttribute(String attributeName, String attributeValue) {
    return new WithAttribute(attributeName, attributeValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By byTagAndContentDescription(String tag, String contentDescriptionValue) {
    return new ByTagAndContentDescription(tag, contentDescriptionValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By withTagAndContentDescription(String tag, String contentDescriptionValue) {
    return new WithTagAndContentDescription(tag, contentDescriptionValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By byContentDescription(String contentDescriptionValue) {
    return new ByContentDescription(contentDescriptionValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By withContentDescription(String contentDescriptionValue) {
    return new WithContentDescription(contentDescriptionValue);
  }

  @CheckReturnValue
  @Nonnull
  public static By byClassNameAndIndex(String className, int index) {
    return new ByClassNameAndIndex(className, index);
  }
}
