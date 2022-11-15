package com.codeborne.selenide.appium.selector;

public class WithContentDescription extends WithTagAndContentDescription {

  public WithContentDescription(String contentDescriptionValue) {
    super("*", contentDescriptionValue);
  }

}
