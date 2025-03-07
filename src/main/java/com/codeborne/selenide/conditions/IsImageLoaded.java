package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import static java.util.Objects.requireNonNull;

public class IsImageLoaded extends WebElementCondition {
  private static final JavaScript js = new JavaScript("is-image.js");

  public IsImageLoaded() {
    super("image");
  }

  @Override
  public CheckResult check(Driver driver, WebElement webElement) {
    boolean image = isImage(driver, webElement);
    return new CheckResult(image, image);
  }

  public static boolean isImage(Driver driver, WebElement webElement) {
    return requireNonNull(js.execute(driver, webElement));
  }
}
