package com.codeborne.selenide.appium;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.WebElementCommunicator;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.appium.WebdriverUnwrapper.instanceOf;

@ParametersAreNonnullByDefault
public class AppiumElementCommunicator extends WebElementCommunicator {

  @Override
  public List<String> texts(Driver driver, List<WebElement> elements) {
    if (instanceOf(driver, AppiumDriver.class)) {
      return textsOneByOne(elements);
    }
    return super.texts(driver, elements);
  }

  @Override
  public List<String> attributes(Driver driver, List<WebElement> elements, String attributeName) {
    if (instanceOf(driver, AppiumDriver.class)) {
      return attributesOneByOne(elements, attributeName);
    }
    return super.attributes(driver, elements, attributeName);
  }
}
