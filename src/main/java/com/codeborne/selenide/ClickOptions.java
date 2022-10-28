package com.codeborne.selenide;

import com.codeborne.selenide.impl.HasTimeout;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ClickMethod.JS;

@ParametersAreNonnullByDefault
public class ClickOptions implements HasTimeout {
  private final int offsetX;
  private final int offsetY;
  private final ClickMethod clickMethod;
  @Nullable
  private final Duration timeout;

  protected ClickOptions(ClickMethod clickMethod, int offsetX, int offsetY, @Nullable Duration timeout) {
    this.clickMethod = clickMethod;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.timeout = timeout;
  }

  @CheckReturnValue
  @Nonnull
  public static ClickOptions usingDefaultMethod() {
    return new ClickOptions(DEFAULT, 0, 0, null);
  }

  @CheckReturnValue
  @Nonnull
  public static ClickOptions usingJavaScript() {
    return new ClickOptions(JS, 0, 0, null);
  }

  @CheckReturnValue
  @Nonnull
  public static ClickOptions withOffset(int offsetX, int offsetY) {
    return new ClickOptions(DEFAULT, offsetX, offsetY, null);
  }

  @CheckReturnValue
  @Nonnull
  public static ClickOptions withTimeout(Duration timeout) {
    return new ClickOptions(DEFAULT, 0, 0, timeout);
  }

  @CheckReturnValue
  public int offsetX() {
    return offsetX;
  }

  @CheckReturnValue
  public int offsetY() {
    return offsetY;
  }

  @CheckReturnValue
  @Nonnull
  public ClickMethod clickMethod() {
    return clickMethod;
  }

  @CheckReturnValue
  @Nullable
  @Override
  public Duration timeout() {
    return timeout;
  }

  @CheckReturnValue
  @Nonnull
  public ClickOptions offsetX(int offsetX) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout);
  }

  @CheckReturnValue
  @Nonnull
  public ClickOptions offsetY(int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout);
  }

  @CheckReturnValue
  @Nonnull
  public ClickOptions offset(int offsetX, int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout);
  }

  /**
   * <p>
   *  Custom timeout for this click.
   * </p>
   * <p>
   *   It's only reasonable to set this timeout if it's different from `Configuration.pageLoadTimeout` which is by default 30 seconds.
   *   Most probably it's needed to a set a custom timeout when you need to click a link which opens a very-very slow page
   *   which loads in more than 30 seconds.
   * </p>
   * <p>
   *   NB! We strongly recommend to optimize such slow pages instead of increasing timeout.
   *   These pages make your tests slower.
   * </p>
   * @since 6.6.0
   */
  @CheckReturnValue
  @Nonnull
  public ClickOptions timeout(Duration timeout) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout);
  }

  @Override
  public String toString() {
    if (offsetX == 0 && offsetY == 0 && timeout == null)
      return String.format("method: %s", clickMethod);
    else if (timeout == null)
      return String.format("method: %s, offsetX: %s, offsetY: %s", clickMethod, offsetX, offsetY);
    else
      return String.format("method: %s, offsetX: %s, offsetY: %s, timeout: %s", clickMethod, offsetX, offsetY, timeout);
  }
}
