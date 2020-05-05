package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Attribute extends Condition {
  private final String attributeName;

  public Attribute(String attributeName) {
    super("attribute");
    this.attributeName = attributeName;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    return element.getAttribute(attributeName) != null;
  }

  @Override
  public String toString() {
    return getName() + " " + attributeName;
  }
}
