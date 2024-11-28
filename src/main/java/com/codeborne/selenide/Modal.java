package com.codeborne.selenide;

import com.codeborne.selenide.ex.DialogTextMismatch;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Alert;

import static com.codeborne.selenide.ModalOptions.withExpectedText;
import static com.codeborne.selenide.logevents.SelenideLogger.getReadableSubject;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class Modal {
  private final Driver driver;

  public Modal(Driver driver) {
    this.driver = driver;
  }

  @CanIgnoreReturnValue
  public String confirm() {
    return confirm(ModalOptions.none());
  }

  @CanIgnoreReturnValue
  public String confirm(ModalOptions options) {
    return SelenideLogger.get(getLogSubject(options), getReadableSubject("confirm"), () -> {
      Alert alert = driver.switchTo().alert(options.timeout());
      String actualDialogText = alert.getText();
      alert.accept();
      checkDialogText(driver, options.expectedText(), actualDialogText);
      return actualDialogText;
    });
  }

  @CanIgnoreReturnValue
  public String prompt() {
    return prompt(ModalOptions.none(), null);
  }

  @CanIgnoreReturnValue
  public String prompt(@Nullable String inputText) {
    return prompt(ModalOptions.none(), inputText);
  }

  @CanIgnoreReturnValue
  public String prompt(@Nullable String expectedDialogText, @Nullable String inputText) {
    return prompt(withExpectedText(expectedDialogText), inputText);
  }

  @CanIgnoreReturnValue
  public String prompt(ModalOptions options, @Nullable String inputText) {
    String subject = getReadableSubject("prompt", defaultString(inputText));
    return SelenideLogger.get(getLogSubject(options), subject, () -> {
      Alert alert = driver.switchTo().alert(options.timeout());
      String actualDialogText = alert.getText();
      if (inputText != null) {
        alert.sendKeys(inputText);
      }
      alert.accept();
      checkDialogText(driver, options.expectedText(), actualDialogText);
      return actualDialogText;
    });
  }

  @CanIgnoreReturnValue
  public String dismiss() {
    return dismiss(ModalOptions.none());
  }

  @CanIgnoreReturnValue
  public String dismiss(@Nullable String expectedDialogText) {
    return dismiss(withExpectedText(expectedDialogText));
  }

  @CanIgnoreReturnValue
  public String dismiss(ModalOptions options) {
    return SelenideLogger.get(getLogSubject(options), getReadableSubject("dismiss"), () -> {
      Alert alert = driver.switchTo().alert(options.timeout());
      String actualDialogText = alert.getText();
      alert.dismiss();
      checkDialogText(driver, options.expectedText(), actualDialogText);
      return actualDialogText;
    });
  }

  private String getLogSubject(ModalOptions options) {
    return String.format("modal(%s)", options);
  }

  private static void checkDialogText(Driver driver, @Nullable String expectedDialogText, String actualDialogText) {
    if (expectedDialogText != null && !expectedDialogText.equals(actualDialogText)) {
      DialogTextMismatch assertionError = new DialogTextMismatch(expectedDialogText, actualDialogText);
      throw UIAssertionError.wrap(driver, assertionError, driver.config().timeout());
    }
  }
}
