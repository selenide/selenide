package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.WebElementCommunicator;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;

public class AppiumElementCommunicator extends WebElementCommunicator {

  @Override
  public List<String> texts(Driver driver, List<WebElement> elements) {
    if (isMobile(driver)) {
      return textsOneByOne(elements);
    }
    return super.texts(driver, elements);
  }

  @Override
  public List<@Nullable String> attributes(Driver driver, List<WebElement> elements, String attributeName) {
    if (isMobile(driver)) {
      return attributesOneByOne(elements, attributeName);
    }
    return super.attributes(driver, elements, attributeName);
  }
}
