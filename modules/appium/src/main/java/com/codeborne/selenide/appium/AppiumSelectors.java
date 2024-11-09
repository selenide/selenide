package com.codeborne.selenide.appium;

import com.codeborne.selenide.appium.selector.ByAttribute;
import com.codeborne.selenide.appium.selector.ByClassNameAndIndex;
import com.codeborne.selenide.appium.selector.ByContentDescription;
import com.codeborne.selenide.appium.selector.ByName;
import com.codeborne.selenide.appium.selector.ByTagAndAttribute;
import com.codeborne.selenide.appium.selector.ByTagAndContentDescription;
import com.codeborne.selenide.appium.selector.ByTagAndName;
import com.codeborne.selenide.appium.selector.ByTagAndText;
import com.codeborne.selenide.appium.selector.ByText;
import com.codeborne.selenide.appium.selector.WithAttribute;
import com.codeborne.selenide.appium.selector.WithContentDescription;
import com.codeborne.selenide.appium.selector.WithName;
import com.codeborne.selenide.appium.selector.WithTagAndAttribute;
import com.codeborne.selenide.appium.selector.WithTagAndContentDescription;
import com.codeborne.selenide.appium.selector.WithTagAndName;
import com.codeborne.selenide.appium.selector.WithTagAndText;
import com.codeborne.selenide.appium.selector.WithText;
import org.openqa.selenium.By;

public class AppiumSelectors {

  private AppiumSelectors() {
  }

  public static By byTagAndText(String tag, String elementText) {
    return new ByTagAndText(tag, elementText);
  }

  public static By byText(String elementText) {
    return new ByText(elementText);
  }

  public static By withTagAndText(String tag, String elementText) {
    return new WithTagAndText(tag, elementText);
  }

  public static By withText(String elementText) {
    return new WithText(elementText);
  }

  public static By byTagAndName(String tag, String nameAttributeValue) {
    return new ByTagAndName(tag, nameAttributeValue);
  }

  public static By byName(String nameAttributeValue) {
    return new ByName(nameAttributeValue);
  }

  public static By withTagAndName(String tag, String nameAttributeValue) {
    return new WithTagAndName(tag, nameAttributeValue);
  }

  public static By withName(String nameAttributeValue) {
    return new WithName(nameAttributeValue);
  }

  public static By byTagAndAttribute(String tag, String attributeName, String attributeValue) {
    return new ByTagAndAttribute(tag, attributeName, attributeValue);
  }

  public static By byAttribute(String attributeName, String attributeValue) {
    return new ByAttribute(attributeName, attributeValue);
  }

  public static By withTagAndAttribute(String tag, String attributeName, String attributeValue) {
    return new WithTagAndAttribute(tag, attributeName, attributeValue);
  }

  public static By withAttribute(String attributeName, String attributeValue) {
    return new WithAttribute(attributeName, attributeValue);
  }

  public static By byTagAndContentDescription(String tag, String contentDescriptionValue) {
    return new ByTagAndContentDescription(tag, contentDescriptionValue);
  }

  public static By withTagAndContentDescription(String tag, String contentDescriptionValue) {
    return new WithTagAndContentDescription(tag, contentDescriptionValue);
  }

  public static By byContentDescription(String contentDescriptionValue) {
    return new ByContentDescription(contentDescriptionValue);
  }

  public static By withContentDescription(String contentDescriptionValue) {
    return new WithContentDescription(contentDescriptionValue);
  }

  public static By byClassNameAndIndex(String className, int index) {
    return new ByClassNameAndIndex(className, index);
  }
}
