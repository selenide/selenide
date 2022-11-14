package com.codeborne.selenide.appium;

import com.codeborne.selenide.appium.selector.*;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public class AppiumSelectors {

  private AppiumSelectors(){
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
}
