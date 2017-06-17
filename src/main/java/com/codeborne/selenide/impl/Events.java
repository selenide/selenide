package com.codeborne.selenide.impl;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.supportsJavascript;
import static java.util.Arrays.asList;

public class Events {
  public static Events events = new Events();

  Logger log = Logger.getLogger(Events.class.getName());
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
      executeJavaScript(element, event);
    }
    catch (StaleElementReferenceException ignore) {
    }
    catch (Exception e) {
      log.warning("Failed to trigger events " + asList(event) + ": " + Cleanup.of.webdriverExceptionMessage(e));
    }
  }

  void executeJavaScript(WebElement element, String... event) {
    Selenide.executeJavaScript(jsCodeToTriggerEvent, element, event);
  }
}
