package com.codeborne.selenide.impl.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

public class Val implements Command<Object> {
  GetValue getValue = new GetValue();
  SetValue setValue = new SetValue();
  
  @Override
  public Object execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    if (args == null || args.length == 0) {
      return getValue.execute(proxy, locator, NO_ARGS);
    }
    else {
      setValue.execute(proxy, locator, args);
      return proxy;
    }
  }
}
