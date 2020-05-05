package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class MatchAttributeWithValue extends Condition {
  private final String attributeName;
  private final Pattern attributeRegex;

  public MatchAttributeWithValue(String attributeName, String attributeRegex) {
    super("match attribute");
    this.attributeName = attributeName;
    this.attributeRegex = Pattern.compile(attributeRegex);
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return attributeRegex.matcher(getAttributeValue(element)).matches();
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return String.format("%s=\"%s\"", attributeName, getAttributeValue(element));
  }

  @Override
  public String toString() {
    return String.format("%s %s=\"%s\"", getName(), attributeName, attributeRegex);
  }

  private String getAttributeValue(WebElement element) {
    String attr = element.getAttribute(attributeName);
    return attr == null ? "" : attr;
  }
}
