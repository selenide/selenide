package com.codeborne.selenide.appium.selector;

public class WithTagAndText extends WithTagAndAttribute {

  public WithTagAndText(String tag, String elementText) {
    super(tag, "text", elementText);
  }

}
