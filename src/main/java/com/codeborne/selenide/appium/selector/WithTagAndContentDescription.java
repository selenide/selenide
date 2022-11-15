package com.codeborne.selenide.appium.selector;

public class WithTagAndContentDescription extends WithTagAndAttribute {

  public WithTagAndContentDescription(String tag, String contentDescriptionValue) {
    super(tag, "content-desc", contentDescriptionValue);
  }
}
