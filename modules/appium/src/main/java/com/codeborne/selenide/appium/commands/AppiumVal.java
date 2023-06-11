package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.GetValue;
import com.codeborne.selenide.commands.Val;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nullable;

public class AppiumVal extends Val {
  private final GetValue getValue = new GetValue();
  private final AppiumSetValue setValue = new AppiumSetValue();

  public AppiumVal() {
    super(new GetValue(), new AppiumSetValue());
  }

  @Override
  @Nullable
  public Object execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    if (args == null || args.length == 0) {
      return getValue.execute(proxy, locator, NO_ARGS);
    }
    else {
      setValue.execute(proxy, locator, args);
      return proxy;
    }
  }
}
