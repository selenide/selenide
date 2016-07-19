package com.codeborne.selenide.impl;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.logging.Logger;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.WebDriverRunner.supportsJavascript;

public class Events {
  private static final Logger log = Logger.getLogger(Events.class.getName());
  
  public static Events events = new Events();
  
  public void fireChangeEvent(WebElement element) {
    if (supportsJavascript()) {
      fireEvent(element, "change");
    }
    else {
      log.fine("Cannot trigger change event: browser does not support javascript");
    }
  }

  public void fireEvent(WebElement element, final String... event) {
    try {
      final String jsCodeToTriggerEvent =
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
      executeJavaScript(jsCodeToTriggerEvent, element, event);
    } catch (StaleElementReferenceException ignore) {
    }
  }
}
