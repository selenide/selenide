package com.codeborne.selenide;


public class ClipboardService {

  public Clipboard getClipboard(Driver driver) {
    return new DefaultClipboard(driver);
  }

}
