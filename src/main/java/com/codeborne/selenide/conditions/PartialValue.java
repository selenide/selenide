package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class PartialValue extends WebElementCondition {
  private final String expectedValue;

  public PartialValue(String expectedValue) {
    super("partial value");
    this.expectedValue = expectedValue;

    if (isEmpty(expectedValue)) {
      throw new IllegalArgumentException("Argument must not be null or empty string. " +
        "Use $.shouldBe(empty) or $.shouldHave(exactValue(\"\").");
    }
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String value = getValueAttribute(element);
    String actualValue = String.format("value=\"%s\"", value);
    boolean valueMatches = Html.text.contains(value, expectedValue);
    return new CheckResult(valueMatches, actualValue);
  }

  @Override
  public String toString() {
    return String.format("%s \"%s\"", getName(), expectedValue);
  }

  private String getValueAttribute(WebElement element) {
    String attr = element.getAttribute("value");
    return attr == null ? "" : attr;
  }
}
