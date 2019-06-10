package com.codeborne.selenide;

public class HiddenString {

  private final String plainTextValue;
  private final String toStringValue;

  public HiddenString(String plainTextValue, String toStringValue) {
    this.plainTextValue = plainTextValue;
    this.toStringValue = toStringValue;
  }

  public String getPlainTextValue() {
    return plainTextValue;
  }

  @Override
  public String toString() {
    return toStringValue;
  }

}
