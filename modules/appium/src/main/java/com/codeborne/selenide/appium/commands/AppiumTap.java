package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.appium.AppiumClickOptions.tap;

public class AppiumTap extends AppiumClick {

  @Override
  public void execute(WebElementSource locator, final Object @Nullable [] args) {
    var newArgs = (args == null || args.length == 0) ?
      new Object[] {tap()} :
      args;
    super.execute(locator, newArgs);
  }
}
