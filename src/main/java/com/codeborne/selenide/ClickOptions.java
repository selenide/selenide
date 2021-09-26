package com.codeborne.selenide;

import com.codeborne.selenide.impl.FileContent;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ClickOptions {

  void click(Driver driver, WebElement element);

  @CheckReturnValue
  @Nonnull
  static ClickOptions usingDefaultMethod() {
    return ZeroOffsetDefaultMethod.INSTANCE;
  }

  @CheckReturnValue
  @Nonnull
  static ClickOptions usingDefaultMethod(int offsetX, int offsetY) {
    return offsetX == 0 && offsetY == 0
      ? ZeroOffsetDefaultMethod.INSTANCE
      : new DefaultMethod(offsetX, offsetY);
  }

  @CheckReturnValue
  @Nonnull
  static ClickOptions usingJavaScript() {
    return ZeroOffsetJSMethod.INSTANCE;
  }

  @CheckReturnValue
  @Nonnull
  static ClickOptions usingJavaScript(int offsetX, int offsetY) {
    return offsetX == 0 && offsetY == 0
      ? ZeroOffsetJSMethod.INSTANCE
      : new JSMethod(offsetX, offsetY);
  }

  static void clickUsingConfigMethod(Driver driver, WebElement element) {
    (driver.config().clickViaJs()
      ? ClickOptions.usingJavaScript()
      : ClickOptions.usingDefaultMethod()
    ).click(driver, element);
  }

  static void clickUsingConfigMethod(Driver driver, WebElement element,
                                     int offsetX, int offsetY) {
    (driver.config().clickViaJs()
      ? ClickOptions.usingJavaScript(offsetX, offsetY)
      : ClickOptions.usingDefaultMethod(offsetX, offsetY)
    ).click(driver, element);
  }

  @ParametersAreNonnullByDefault
  final class ZeroOffsetDefaultMethod implements ClickOptions {
    private static final ZeroOffsetDefaultMethod INSTANCE = new ZeroOffsetDefaultMethod();

    private ZeroOffsetDefaultMethod() {
    }

    @Override
    public void click(Driver driver, WebElement element) {
      element.click();
    }

    @Override
    public String toString() {
      return "method: DEFAULT";
    }
  }

  @ParametersAreNonnullByDefault
  final class DefaultMethod implements ClickOptions {
    private final int offsetX;
    private final int offsetY;

    private DefaultMethod(int offsetX, int offsetY) {
      this.offsetX = offsetX;
      this.offsetY = offsetY;
    }

    @Override
    public void click(Driver driver, WebElement element) {
      driver.actions()
        .moveToElement(element, this.offsetX, this.offsetY)
        .click()
        .perform();
    }

    @Override
    public String toString() {
      return String.format("method: DEFAULT, offsetX: %s, offsetY: %s", this.offsetX, this.offsetY);
    }
  }

  @ParametersAreNonnullByDefault
  final class ZeroOffsetJSMethod implements ClickOptions {
    private static final ZeroOffsetJSMethod INSTANCE = new ZeroOffsetJSMethod();

    private ZeroOffsetJSMethod() {
    }

    @Override
    public void click(Driver driver, WebElement element) {
      driver.executeJavaScript(
        new FileContent("click.js").content(),
        element, 0, 0
      );
    }

    @Override
    public String toString() {
      return "method: JS";
    }
  }

  @ParametersAreNonnullByDefault
  final class JSMethod implements ClickOptions {
    private final int offsetX;
    private final int offsetY;

    private JSMethod(int offsetX, int offsetY) {
      this.offsetX = offsetX;
      this.offsetY = offsetY;
    }

    @Override
    public void click(Driver driver, WebElement element) {
      driver.executeJavaScript(
        new FileContent("click.js").content(),
        element, this.offsetX, this.offsetY
      );
    }

    @Override
    public String toString() {
      return String.format("method: JS, offsetX: %s, offsetY: %s", this.offsetX, this.offsetY);
    }
  }
}
