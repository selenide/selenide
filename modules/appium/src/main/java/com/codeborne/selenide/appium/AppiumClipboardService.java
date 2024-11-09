package com.codeborne.selenide.appium;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.ClipboardService;
import com.codeborne.selenide.Driver;

public class AppiumClipboardService extends ClipboardService {
  @Override
  public Clipboard getClipboard(Driver driver) {
    return new AppiumClipboard(driver);
  }
}
