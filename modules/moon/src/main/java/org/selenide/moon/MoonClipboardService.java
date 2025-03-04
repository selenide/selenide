package org.selenide.moon;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.ClipboardService;
import com.codeborne.selenide.Driver;

public class MoonClipboardService extends ClipboardService {
  @Override
  public Clipboard getClipboard(Driver driver) {
    return new MoonClipboard(driver);
  }
}
