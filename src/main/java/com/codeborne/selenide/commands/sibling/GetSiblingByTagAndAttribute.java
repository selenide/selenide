package com.codeborne.selenide.commands.sibling;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public class GetSiblingByTagAndAttribute implements Sibling {

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String tag = (String) args[0];
    String attributeName = (String) args[1];
    String attributeValue = (String) args[2];
    return locator.find(proxy, By.xpath(String.format("following-sibling::%s[@%s='%s']",
      tag, attributeName, attributeValue)), 0);
  }

  @Override
  public boolean canExecute(@Nonnull Object[] args) {
    return args.length == 3
      && args[0] instanceof String
      && args[1] instanceof String
      && args[2] instanceof String;
  }
}
