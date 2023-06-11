package com.codeborne.selenide.appium.selector;

public class ByContentDescription extends ByTagAndContentDescription {

  public ByContentDescription(String contentDescriptionValue) {
    super("*", contentDescriptionValue);
  }

}
