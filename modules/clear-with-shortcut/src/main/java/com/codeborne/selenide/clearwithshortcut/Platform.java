package com.codeborne.selenide.clearwithshortcut;

import org.jspecify.annotations.Nullable;

import static java.util.Locale.ROOT;
import static org.openqa.selenium.Keys.COMMAND;
import static org.openqa.selenium.Keys.CONTROL;

/**
 * An operating system on which the browser is being executed.
 * May be different from current OS (where the tests are being executed).
 */
public class Platform {
  public static final Platform UNKNOWN = new Platform("?");

  private final String platform;

  Platform(@Nullable String platform) {
    this.platform = platform == null || platform.trim().isEmpty() ? "?" : platform.trim().toLowerCase(ROOT);
  }

  boolean isUnknown() {
    return "?".equals(platform);
  }

  boolean isApple() {
    if (isUnknown()) {
      throw new IllegalStateException("Unknown platform");
    }
    return platform.contains("mac") || platform.contains("iphone");
  }

  public CharSequence modifierKey() {
    return isApple() ? COMMAND : CONTROL;
  }
}
