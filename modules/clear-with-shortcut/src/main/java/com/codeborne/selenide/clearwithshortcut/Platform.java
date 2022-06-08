package com.codeborne.selenide.clearwithshortcut;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Locale.ROOT;
import static org.openqa.selenium.Keys.COMMAND;
import static org.openqa.selenium.Keys.CONTROL;

/**
 * An operating system on which the browser is being executed.
 * May be different from current OS (where the tests are being executed).
 *
 * @since 6.6.0
 */
@ParametersAreNonnullByDefault
class Platform {
  public static final Platform UNKNOWN = new Platform("?");

  private final String platform;

  Platform(@Nullable String platform) {
    this.platform = platform == null || platform.trim().isEmpty() ? "?" : platform.trim().toLowerCase(ROOT);
  }

  @CheckReturnValue
  boolean isUnknown() {
    return "?".equals(platform);
  }

  @CheckReturnValue
  boolean isApple() {
    if (isUnknown()) {
      throw new IllegalStateException("Unknown platform");
    }
    return platform.contains("mac") || platform.contains("iphone");
  }

  @CheckReturnValue
  @Nonnull
  public CharSequence modifierKey() {
    return isApple() ? COMMAND : CONTROL;
  }
}
