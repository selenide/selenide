package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

public class Val implements Command<Object> {
  private final GetValue getValue;
  private final SetValue setValue;

  public Val() {
    this.getValue = new GetValue();
    this.setValue = new SetValue();
  }

  protected Val(GetValue getValue, SetValue setValue) {
    this.getValue = getValue;
    this.setValue = setValue;
  }

  @Override
  @Nullable
  public Object execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    if (args == null || args.length == 0) {
      return getValue.execute(proxy, locator, NO_ARGS);
    }
    else {
      setValue.execute(proxy, locator, args);
      return proxy;
    }
  }
}
