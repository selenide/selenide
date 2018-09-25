package com.codeborne.selenide;

import com.codeborne.selenide.ex.DialogTextMismatch;
import org.openqa.selenium.Alert;

public class Modal {
  private final Driver driver;

  public Modal(Driver driver) {
    this.driver = driver;
  }

  public String confirm() {
    return confirm(null);
  }

  public String confirm(String expectedDialogText) {
    Alert alert = driver.switchTo().alert();
    String actualDialogText = alert.getText();
    alert.accept();
    checkDialogText(driver, expectedDialogText, actualDialogText);
    return actualDialogText;
  }

  public String prompt() {
    return prompt(null, null);
  }

  public String prompt(String inputText) {
    return prompt(null, inputText);
  }

  public String prompt(String expectedDialogText, String inputText) {
    Alert alert = driver.switchTo().alert();
    String actualDialogText = alert.getText();
    if (inputText != null)
      alert.sendKeys(inputText);
    alert.accept();
    checkDialogText(driver, expectedDialogText, actualDialogText);
    return actualDialogText;
  }

  public String dismiss() {
    return dismiss(null);
  }

  public String dismiss(String expectedDialogText) {
    Alert alert = driver.switchTo().alert();
    String actualDialogText = alert.getText();
    alert.dismiss();
    checkDialogText(driver, expectedDialogText, actualDialogText);
    return actualDialogText;
  }

  private static void checkDialogText(Driver driver, String expectedDialogText, String actualDialogText) {
    if (expectedDialogText != null && !expectedDialogText.equals(actualDialogText)) {
      throw new DialogTextMismatch(driver, actualDialogText, expectedDialogText);
    }
  }
}
