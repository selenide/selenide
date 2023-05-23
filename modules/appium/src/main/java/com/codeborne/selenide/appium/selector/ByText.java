package com.codeborne.selenide.appium.selector;

public class ByText extends ByTagAndText {

  public ByText(String elementText) {
    super("*", elementText);
  }

}
