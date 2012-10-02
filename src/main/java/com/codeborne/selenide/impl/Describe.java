package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

class Describe {
  private WebElement element;
  private StringBuilder sb = new StringBuilder();

  Describe(WebElement element) {
    this.element = element;
    sb.append('<').append(element.getTagName());
  }

  Describe attr(String attributeName) {
    String attributeValue = element.getAttribute(attributeName);
    if (attributeValue != null) sb.append(' ').append(attributeName).append('=').append(attributeValue);
    return this;
  }

  @Override
  public String toString() {
    sb.append('>').append(element.getText()).append("</").append(element.getTagName()).append('>');
    return sb.toString();
  }
}