package com.codeborne.selenide.appium.selector;

public class WithText extends WithTagAndText {
  public WithText(String elementText) {
    super("*", elementText);
  }
}
