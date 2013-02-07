package com.codeborne.selenide;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.DOM.$;
import static com.codeborne.selenide.DOM.$$;
import static com.codeborne.selenide.impl.ShouldableWebElementProxy.wrap;

public class Selenide {
  public static Selenide $ = new Selenide();

  /**
   * Select radio field by value
   * @param radioField any By selector for finding radio field
   * @param value value to select (should match an attribute "value")
   * @return the selected radio field
   */
  public ShouldableWebElement selectRadio(By radioField, String value) {
    $(radioField).shouldBe(enabled);
    for (WebElement radio : $$(radioField)) {
      if (value.equals(radio.getAttribute("value"))) {
        radio.click();
        return wrap(radio);
      }
    }
    throw new NoSuchElementException(radioField + " and value " + value);
  }

  public ShouldableWebElement getSelectedRadio(By radioField) {
    for (WebElement radio : $$(radioField)) {
      if (radio.getAttribute("checked") != null) {
        return wrap(radio);
      }
    }
    return null;
  }
}
