package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IsImageLoaded extends Condition {

  public IsImageLoaded() {
    super("is image");
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement webElement) {
    boolean image = isImage(driver, webElement);
    return new CheckResult(image, image);
  }

  public static boolean isImage(Driver driver, WebElement webElement) {
    return driver.executeJavaScript("return arguments[0].tagName.toLowerCase() === 'img' && " +
      "arguments[0].complete && " +
      "typeof arguments[0].naturalWidth != 'undefined' && " +
      "arguments[0].naturalWidth > 0", webElement);
  }
}
