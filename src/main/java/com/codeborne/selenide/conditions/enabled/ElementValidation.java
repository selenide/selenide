package com.codeborne.selenide.conditions.enabled;

import org.openqa.selenium.WebElement;

import java.util.Objects;

public class ElementValidation {

  public void validaElementIsApplicableForEnabledCondition(WebElement element) {
    String elementTag = element.getTagName();
    if (!Objects.equals(elementTag, "input")) {
      throw new IllegalArgumentException(
        "This condition can be applied to the input element element only. Given element is: " + elementTag
      );
    }
  }
}
