package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public class Animated extends WebElementCondition {
  private static final JavaScript js = new JavaScript("animation.js");

  public Animated() {
    super("animated");
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    Map<String, Object> result = requireNonNull(js.execute(driver, element));
    if (result.get("error") != null) {
      throw new IllegalStateException((String) result.get("error"));
    }
    boolean animating = (boolean) result.get("animating");
    return new CheckResult(animating, animating ? "animated" : "not animated");
  }
}
