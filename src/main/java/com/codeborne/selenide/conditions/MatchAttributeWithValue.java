package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;

import java.util.regex.Pattern;

public class MatchAttributeWithValue extends WebElementCondition {
  private final String attributeName;
  private final Pattern attributeRegex;

  public MatchAttributeWithValue(String attributeName, String attributeRegex) {
    super("match attribute");
    this.attributeName = attributeName;
    this.attributeRegex = Pattern.compile(attributeRegex);
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String attributeValue = getAttributeValue(element);
    return new CheckResult(attributeRegex.matcher(attributeValue).matches(), String.format("%s~/%s/", attributeName, attributeValue));
  }

  @Override
  public String toString() {
    return String.format("%s %s~/%s/", getName(), attributeName, attributeRegex);
  }

  private String getAttributeValue(WebElement element) {
    String attr = element.getAttribute(attributeName);
    return attr == null ? "" : attr;
  }
}
