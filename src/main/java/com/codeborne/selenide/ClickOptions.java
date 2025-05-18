package com.codeborne.selenide;

import com.codeborne.selenide.impl.HasTimeout;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ClickMethod.JS;
import static com.codeborne.selenide.commands.Util.merge;
import static java.util.Collections.emptyList;

public class ClickOptions implements HasTimeout {
  private final int offsetX;
  private final int offsetY;
  private final ClickMethod clickMethod;
  @Nullable
  private final Duration timeout;
  private final boolean force;
  private final List<Keys> holdingKeys;

  protected ClickOptions(ClickMethod clickMethod, int offsetX, int offsetY, @Nullable Duration timeout, boolean force,
                         List<Keys> holdingKeys) {
    this.clickMethod = clickMethod;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.timeout = timeout;
    this.force = force;
    this.holdingKeys = holdingKeys;
  }

  public static ClickOptions using(ClickMethod method) {
    return new ClickOptions(method, 0, 0, null, false, emptyList());
  }

  public static ClickOptions usingDefaultMethod() {
    return using(DEFAULT);
  }

  public static ClickOptions usingJavaScript() {
    return using(JS);
  }

  public static ClickOptions withOffset(int offsetX, int offsetY) {
    return new ClickOptions(DEFAULT, offsetX, offsetY, null, false, emptyList());
  }

  public static ClickOptions withTimeout(Duration timeout) {
    return new ClickOptions(DEFAULT, 0, 0, timeout, false, emptyList());
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

  public List<Keys> holdingKeys() {
    return holdingKeys;
  }

  public ClickOptions offsetX(int offsetX) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, force, holdingKeys);
  }

  public ClickOptions offsetY(int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, force, holdingKeys);
  }

  public ClickOptions offset(int offsetX, int offsetY) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, force, holdingKeys);
  }

  /**
   * <p>
   * Custom timeout for this click.
   * </p>
   * <p>
   * It's only reasonable to set this timeout if it's different from `Configuration.pageLoadTimeout` which is by default 30 seconds.
   * Most probably it's needed to a set a custom timeout when you need to click a link which opens a very-very slow page
   * which loads in more than 30 seconds.
   * </p>
   * <p>
   * NB! We strongly recommend to optimize such slow pages instead of increasing timeout.
   * These pages make your tests slower.
   * </p>
   */
  public ClickOptions timeout(Duration timeout) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, force, holdingKeys);
  }

  public ClickOptions force() {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, true, holdingKeys);
  }

  public ClickOptions holdingKeys(Keys holdingKeys, Keys... otherKeys) {
    return new ClickOptions(clickMethod, offsetX, offsetY, timeout, force, merge(holdingKeys, otherKeys));
  }

  @Override
  public String toString() {
    if (hasDefaultParameters())
      return String.format("method: %s", clickMethod);
    else if (timeout == null)
      return String.format("method: %s, offsetX: %s, offsetY: %s, force: %s, keys: %s",
        clickMethod, offsetX, offsetY, force, keysToString());
    else
      return String.format("method: %s, offsetX: %s, offsetY: %s, timeout: %s, force: %s, keys: %s",
        clickMethod, offsetX, offsetY, timeout, force, keysToString());
  }

  private boolean hasDefaultParameters() {
    return offsetX == 0 && offsetY == 0 && !force && timeout == null && holdingKeys.isEmpty();
  }

  private String keysToString() {
    return holdingKeys.stream().map(this::keyToString).toList().toString();
  }

  private String keyToString(Keys key) {
    return switch (key) {
      case ALT -> "alt";
      case LEFT_ALT -> "left alt";
      case CONTROL -> "control";
      case LEFT_CONTROL -> "left control";
      case SHIFT -> "shift";
      case LEFT_SHIFT -> "left shift";
      case META -> "meta";
      default -> key.toString();
    };
  }
}
