package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class GetCss implements Command<Map<String, String>> {
  private static final JavaScript js = new JavaScript("get-css.js");

  @Override
  @CheckReturnValue
  @Nonnull
  public Map<String, String> execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    return js.execute(locator.driver(), locator.getWebElement());
  }
}
