package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import com.codeborne.selenide.ex.DialogTextMismatch;
import org.openqa.selenium.Alert;

public class Modals {

  public String confirm(Context context) {
    return confirm(context, null);
  }

  public String confirm(Context context, String expectedDialogText) {
    Alert alert = context.switchTo().alert();
    String actualDialogText = alert.getText();
    alert.accept();
    checkDialogText(expectedDialogText, actualDialogText);
    return actualDialogText;
  }

  public String prompt(Context context) {
    return prompt(context, null, null);
  }

  public String prompt(Context context, String inputText) {
    return prompt(context, null, inputText);
  }

  public String prompt(Context context, String expectedDialogText, String inputText) {
    Alert alert = context.switchTo().alert();
    String actualDialogText = alert.getText();
    if (inputText != null)
      alert.sendKeys(inputText);
    alert.accept();
    checkDialogText(expectedDialogText, actualDialogText);
    return actualDialogText;
  }

  public String dismiss(Context context) {
    return dismiss(context, null);
  }

  public String dismiss(Context context, String expectedDialogText) {
    Alert alert = context.switchTo().alert();
    String actualDialogText = alert.getText();
    alert.dismiss();
    checkDialogText(expectedDialogText, actualDialogText);
    return actualDialogText;
  }

  private static void checkDialogText(String expectedDialogText, String actualDialogText) {
    if (expectedDialogText != null && !expectedDialogText.equals(actualDialogText)) {
      throw new DialogTextMismatch(actualDialogText, expectedDialogText);
    }
  }
}
