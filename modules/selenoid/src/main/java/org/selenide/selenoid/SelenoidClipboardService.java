package org.selenide.selenoid;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.ClipboardService;
import com.codeborne.selenide.Driver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SelenoidClipboardService extends ClipboardService {
  @Nonnull
  @CheckReturnValue
  @Override
  public Clipboard getClipboard(Driver driver) {
    return new SelenoidClipboard(driver);
  }
}
