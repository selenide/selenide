package com.codeborne.selenide.appium.selector;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Quotes;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public class WithTagAndAttribute extends By.ByXPath {

  protected final String tag;
  protected final String attributeName;
  protected final String attributeValue;

  public WithTagAndAttribute(String tag, String attributeName, String attributeValue) {
    super(".//" + tag + "[contains(@" + attributeName + "," + Quotes.escape(attributeValue) + ")]");
    this.tag = tag;
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "*".equals(tag) ?
      String.format("[%s*=%s]", attributeName, attributeValue) :
      String.format("%s[%s*=%s]", tag, attributeName, attributeValue);
  }
}
