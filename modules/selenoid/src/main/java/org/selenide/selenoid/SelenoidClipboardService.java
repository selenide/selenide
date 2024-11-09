package org.selenide.selenoid;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.ClipboardService;
import com.codeborne.selenide.Driver;

public class SelenoidClipboardService extends ClipboardService {
  @Override
  public Clipboard getClipboard(Driver driver) {
    return new SelenoidClipboard(driver);
  }
}
