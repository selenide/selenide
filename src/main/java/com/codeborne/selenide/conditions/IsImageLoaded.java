package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IsImageLoaded extends Condition {
  private static final JavaScript js = new JavaScript("is-image.js");

  public IsImageLoaded() {
    super("image");
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement webElement) {
    boolean image = isImage(driver, webElement);
    return new CheckResult(image, image);
  }

  public static boolean isImage(Driver driver, WebElement webElement) {
    return js.execute(driver, webElement);
  }
}
