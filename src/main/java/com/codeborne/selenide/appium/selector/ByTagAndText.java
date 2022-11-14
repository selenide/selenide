package com.codeborne.selenide.appium.selector;

public class ByTagAndText extends ByTagAndAttribute {

  public ByTagAndText(String tag, String elementText) {
    super(tag, "text", elementText);
  }

}
