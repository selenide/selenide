package com.codeborne.selenide.commands;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import static java.util.Objects.requireNonNull;

public final class GetVisibleText {
  private static final JavaScript js = new JavaScript("visible-text.js");

  private GetVisibleText() {
  }

  public static String getVisibleText(Driver driver, WebElement element, GetSelectedOptionText getSelectedOptionText) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionText.execute(driver, element) :
      requireNonNull(js.execute(driver, element));
  }
}
