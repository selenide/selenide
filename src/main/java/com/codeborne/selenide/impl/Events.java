package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.logging.Logger;

import static java.util.Arrays.asList;

public class Events {
  public static Events events = new Events();

  Logger log = Logger.getLogger(Events.class.getName());
  private final String jsCodeToTriggerEvent =
      "var webElement = arguments[0];\n" +
          "var eventNames = arguments[1];\n" +
          "for (var i = 0; i < eventNames.length; i++) {" +
          "  if (document.createEventObject) {\n" +  // IE
          "    var evt = document.createEventObject();\n" +
          "    webElement.fireEvent('on' + eventNames[i], evt);\n" +
          "  }\n" +
          "  else {\n" +
          "    var evt = document.createEvent('HTMLEvents');\n " +
          "    evt.initEvent(eventNames[i], true, true );\n " +
          "    webElement.dispatchEvent(evt);\n" +
          "  }\n" +
          '}';

  public void fireEvent(Driver driver, WebElement element, final String... event) {
    try {
      executeJavaScript(driver, element, event);
    }
    catch (StaleElementReferenceException ignore) {
    }
    catch (Exception e) {
      log.warning("Failed to trigger events " + asList(event) + ": " + Cleanup.of.webdriverExceptionMessage(e));
    }
  }

  void executeJavaScript(Driver driver, WebElement element, String... event) {
    driver.executeJavaScript(jsCodeToTriggerEvent, element, event);
  }
}
