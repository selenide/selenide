package com.codeborne.selenide;

import com.codeborne.selenide.impl.HasTimeout;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ClickMethod.JS;

public class ClickOptions implements HasTimeout {
  private final int offsetX;
  private final int offsetY;
  private final ClickMethod clickMethod;
  @Nullable
  private final Duration timeout;
  private final boolean force;

  protected ClickOptions(ClickMethod clickMethod, int offsetX, int offsetY, @Nullable Duration timeout, boolean force) {
    this.clickMethod = clickMethod;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.timeout = timeout;
    this.force = force;
  }

  public static ClickOptions usingDefaultMethod() {
    return new ClickOptions(DEFAULT, 0, 0, null, false);
  }

  public static ClickOptions usingJavaScript() {
    return new ClickOptions(JS, 0, 0, null, false);
  }

  public static ClickOptions withOffset(int offsetX, int offsetY) {
    return new ClickOptions(DEFAULT, offsetX, offsetY, null, false);
  }

  public static ClickOptions withTimeout(Duration timeout) {
    return new ClickOptions(DEFAULT, 0, 0, timeout, false);
  }

  public int offsetX() {
    return offsetX;
  }

  public int offsetY() {
    return offsetY;
  }

  public ClickMethod clickMethod() {
    return clickMethod;
  }

  @Nullable
  @Override
  public Duration timeout() {
    return timeout;
  }

  public boolean isForce() {
    return force;
  }

  public ClickOptions offsetX(int offsetX) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, force);
  }

  public ClickOptions offsetY(int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, force);
  }

  public ClickOptions offset(int offsetX, int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, force);
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
   */
  public ClickOptions timeout(Duration timeout) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, force);
  }

  public ClickOptions force() {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, true);
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
