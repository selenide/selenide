package com.codeborne.selenide.impl;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.ex.DialogTextMismatch;
import org.openqa.selenium.Alert;

import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.dismissModalDialogs;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.supportsModalDialogs;

public class Modals {
  private static final Logger log = Logger.getLogger(Modals.class.getName());

  private boolean doDismissModalDialogs() {
    return !supportsModalDialogs() || dismissModalDialogs;
  }

  public void mockModalDialogs() {
    if (doDismissModalDialogs()) {
      String jsCode =
        "  window._selenide_modalDialogReturnValue = true;\n" +
          "  window.alert = function(message) {};\n" +
          "  window.confirm = function(message) {\n" +
          "    return window._selenide_modalDialogReturnValue;\n" +
          "  };";
      try {
        executeJavaScript(jsCode);
      }
      catch (UnsupportedOperationException cannotExecuteJsAgainstPlainTextPage) {
        log.warning(cannotExecuteJsAgainstPlainTextPage.toString());
      }
    }
  }

  public void onConfirmReturn(boolean confirmReturnValue) {
    if (doDismissModalDialogs()) {
      executeJavaScript("window._selenide_modalDialogReturnValue = " + confirmReturnValue + ';');
    }
  }

  public String confirm() {
    return confirm(null);
  }

  public String confirm(String expectedDialogText) {
    if (!doDismissModalDialogs()) {
      Alert alert = switchTo().alert();
      String actualDialogText = alert.getText();
      alert.accept();
      checkDialogText(expectedDialogText, actualDialogText);
      return actualDialogText;
    }
    return null;
  }

  public String prompt() {
    return prompt(null, null);
  }

  public String prompt(String inputText) {
    return prompt(null, inputText);
  }

  public String prompt(String expectedDialogText, String inputText) {
    if (!doDismissModalDialogs()) {
      Alert alert = switchTo().alert();
      String actualDialogText = alert.getText();
      if (inputText != null)
        alert.sendKeys(inputText);
      alert.accept();
      checkDialogText(expectedDialogText, actualDialogText);
      return actualDialogText;
    }
    return null;
  }

  public String dismiss() {
    return dismiss(null);
  }

  public String dismiss(String expectedDialogText) {
    if (!doDismissModalDialogs()) {
      Alert alert = switchTo().alert();
      String actualDialogText = alert.getText();
      alert.dismiss();
      checkDialogText(expectedDialogText, actualDialogText);
      return actualDialogText;
    }
    return null;
  }

  private static void checkDialogText(String expectedDialogText, String actualDialogText) {
    if (expectedDialogText != null && !expectedDialogText.equals(actualDialogText)) {
      Screenshots.takeScreenShot(Selenide.class.getName(), Thread.currentThread().getName());
      throw new DialogTextMismatch(actualDialogText, expectedDialogText);
    }
  }
}
