package com.codeborne.selenide.appium;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.ClipboardService;
import com.codeborne.selenide.Driver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @since 1.6.10
 */
@ParametersAreNonnullByDefault
public class AppiumClipboardService extends ClipboardService {
  @Nonnull
  @CheckReturnValue
  @Override
  public Clipboard getClipboard(Driver driver) {
    return new AppiumClipboard(driver);
  }
}
