package com.codeborne.selenide.selector;

public class WithText extends WithTagAndText {
  public WithText(String elementText) {
    super("*", elementText);
  }

  @Override
  public String toString() {
    return "with text: " + elementText;
  }
}
