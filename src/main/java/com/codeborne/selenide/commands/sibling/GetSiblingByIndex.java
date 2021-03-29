package com.codeborne.selenide.commands.sibling;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public class GetSiblingByIndex implements Sibling {

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    int index = (int) args[0] + 1;
    return locator.find(proxy, By.xpath(String.format("following-sibling::*[%d]", index)), 0);
  }

  @Override
  public boolean canExecute(@Nonnull Object[] args) {
    return args.length == 1
      && args[0] instanceof Integer;
  }
}
