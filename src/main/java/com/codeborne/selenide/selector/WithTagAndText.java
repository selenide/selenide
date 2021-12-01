package com.codeborne.selenide.selector;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Quotes;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.selector.Xpath.NORMALIZE_SPACE_XPATH;

@ParametersAreNonnullByDefault
public class WithTagAndText extends By.ByXPath {
  protected final String tag;
  protected final String elementText;

  public WithTagAndText(String tag, String elementText) {
    super(".//" + tag + "/text()[contains(" + NORMALIZE_SPACE_XPATH + ", " + Quotes.escape(elementText) + ")]/parent::*");
    this.tag = tag;
    this.elementText = elementText;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "by tag: " + tag + "; with text: " + elementText;
  }

  @CheckReturnValue
  @Nonnull
  String getXPath() {
    return super.toString().replace("By.xpath: ", "");
  }
}
