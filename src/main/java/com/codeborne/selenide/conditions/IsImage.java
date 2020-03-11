package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

public class IsImage extends Condition {

  public IsImage() {
    super("is image");
  }

  @Override
  public boolean apply(Driver driver, WebElement webElement) {
    if (!"img".equalsIgnoreCase(webElement.getTagName())) {
      return false;
    }
    return driver.executeJavaScript("return arguments[0].complete && " +
      "typeof arguments[0].naturalWidth != 'undefined' && " +
      "arguments[0].naturalWidth > 0", webElement);
  }
}
