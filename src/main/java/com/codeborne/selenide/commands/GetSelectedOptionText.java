package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class GetSelectedOptionText implements Command<String> {
  private final JavaScript js = new JavaScript("get-selected-option-text.js");

  @Override
  @CheckReturnValue
  @Nonnull
  public String execute(SelenideElement proxy, WebElementSource selectElement, @Nullable Object[] args) {
    List<String> result = js.execute(selectElement.driver(), selectElement.getWebElement());
    if (result.get(1) != null) {
      throw new IllegalArgumentException(result.get(1));
    }
    return result.get(0);
  }
}
