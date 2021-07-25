package com.codeborne.selenide.selector;

import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ByAttribute extends By.ByCssSelector {
  public ByAttribute(String attributeName, String attributeValue) {
    super(String.format("[%s=\"%s\"]", attributeName, escapeAttributeValue(attributeValue)));
  }

  private static String escapeAttributeValue(String attributeValue) {
    return attributeValue.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
  }
}
