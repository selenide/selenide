package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ClipboardService {
  @CheckReturnValue
  @Nonnull
  public Clipboard getClipboard(Driver driver) {
    return driver.supportsJavascript() ? new JSClipboard(driver) : new AwtClipboard(driver);
  }
}
