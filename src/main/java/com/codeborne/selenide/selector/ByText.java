package com.codeborne.selenide.selector;

public class ByText extends ByTagAndText {
  public ByText(String elementText) {
    super("*", elementText);
  }

  @Override
  public String toString() {
    return "by text: " + elementText;
  }
}
