package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.captureJavascriptErrors;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class JavascriptErrorsCollector {
  private static final Logger log = Logger.getLogger(JavascriptErrorsCollector.class.getName());

  public void collectJavascriptErrors(WebDriver webdriver) {
    if (webdriver instanceof JavascriptExecutor && captureJavascriptErrors) {
      collectJavascriptErrors((JavascriptExecutor) webdriver);
    }
  }

  private void collectJavascriptErrors(JavascriptExecutor webdriver) {
    try {
      webdriver.executeScript(
        "if (!window._selenide_jsErrors) {\n" +
          "  window._selenide_jsErrors = [];\n" +
          "}\n" +
          "if (!window.onerror) {\n" +
          "  window.onerror = function (errorMessage, url, lineNumber) {\n" +
          "    var message = errorMessage + ' at ' + url + ':' + lineNumber;\n" +
          "    window._selenide_jsErrors.push(message);\n" +
          "    return false;\n" +
          "  };\n" +
          "}\n"
      );
    }
    catch (UnsupportedOperationException cannotExecuteJsAgainstPlainTextPage) {
      log.warning(cannotExecuteJsAgainstPlainTextPage.toString());
    }
    catch (WebDriverException cannotExecuteJs) {
      log.severe(cannotExecuteJs.toString());
    }
  }

  public List<String> getJavascriptErrors(Context context) {
    if (!captureJavascriptErrors) {
      return emptyList();
    }
    else if (!context.hasWebDriverStarted()) {
      return emptyList();
    }
    else if (!context.supportsJavascript()) {
      return emptyList();
    }
    try {
      Object errors = context.executeJavaScript("return window._selenide_jsErrors");
      if (errors == null) {
        return emptyList();
      }
      else if (errors instanceof List) {
        return errorsFromList((List<Object>) errors);
      }
      else if (errors instanceof Map) {
        return errorsFromMap((Map<Object, Object>) errors);
      }
      else {
        return asList(errors.toString());
      }
    } catch (WebDriverException | UnsupportedOperationException cannotExecuteJs) {
      log.warning(cannotExecuteJs.toString());
      return emptyList();
    }
  }

  private static List<String> errorsFromList(List<Object> errors) {
    if (errors.isEmpty()) {
      return emptyList();
    }
    List<String> result = new ArrayList<>(errors.size());
    for (Object error : errors) {
      result.add(error.toString());
    }
    return result;
  }

  private static List<String> errorsFromMap(Map<Object, Object> errors) {
    if (errors.isEmpty()) {
      return emptyList();
    }
    List<String> result = new ArrayList<>(errors.size());
    for (Map.Entry error : errors.entrySet()) {
      result.add(error.getKey() + ": " + error.getValue());
    }
    return result;
  }
}
