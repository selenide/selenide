package com.codeborne.selenide.files;

import com.codeborne.selenide.Modal;

import static com.codeborne.selenide.ModalOptions.withExpectedText;

public class DownloadActions {
  public static DownloadAction click() {
    return (driver, link) -> {
      link.click();
    };
  }

  public static DownloadAction clickAndConfirm() {
    return (driver, link) -> {
      link.click();
      new Modal(driver).confirm();
    };
  }

  public static DownloadAction clickAndConfirm(String expectedAlertText) {
    return (driver, link) -> {
      link.click();
      new Modal(driver).confirm(withExpectedText(expectedAlertText));
    };
  }
}
