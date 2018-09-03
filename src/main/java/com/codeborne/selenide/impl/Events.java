package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.logging.Logger;

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

  public void fireChangeEvent(Context context, WebElement element) {
    if (context.supportsJavascript()) {
      fireEvent(context, element, "change");
    }
    else {
      log.fine("Cannot trigger change event: browser does not support javascript");
    }
  }

  public void fireEvent(Context context, WebElement element, final String... event) {
    try {
      executeJavaScript(context, element, event);
    }
    catch (StaleElementReferenceException ignore) {
    }
    catch (Exception e) {
      log.warning("Failed to trigger events " + asList(event) + ": " + Cleanup.of.webdriverExceptionMessage(e));
    }
  }

  void executeJavaScript(Context context, WebElement element, String... event) {
    context.executeJavaScript(jsCodeToTriggerEvent, element, event);
  }
}
