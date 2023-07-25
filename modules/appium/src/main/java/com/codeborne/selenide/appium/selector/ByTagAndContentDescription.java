package com.codeborne.selenide.appium.selector;

public class ByTagAndContentDescription extends ByTagAndAttribute {

  public ByTagAndContentDescription(String tag, String contentDescriptionValue) {
    super(tag, "content-desc", contentDescriptionValue);
  }

}
