package com.codeborne.selenide;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ClipboardService {
  public Clipboard getClipboard(Driver driver) {
    return new DefaultClipboard(driver);
  }
}
