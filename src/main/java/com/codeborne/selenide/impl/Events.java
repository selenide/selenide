package com.codeborne.selenide.impl;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class Events {
  public static Events events = new Events();
  
  public void fireChangeEvent(WebElement element) {
    fireEvent(element, "change");
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
