package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GetSelectedText implements Command<String> {
  private final GetSelectedOption getSelectedOption;

  GetSelectedText(@Nonnull GetSelectedOption getSelectedOption) {
    this.getSelectedOption = getSelectedOption;
  }

  public GetSelectedText() {
    this.getSelectedOption = new GetSelectedOption();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String execute(SelenideElement proxy, WebElementSource selectElement, @Nullable Object[] args) {
    WebElement option = getSelectedOption.execute(proxy, selectElement, NO_ARGS);
    return option.getText();
  }
}
