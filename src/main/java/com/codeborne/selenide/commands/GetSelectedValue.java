package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class GetSelectedValue implements Command<String> {
  private final Command<SelenideElement> getSelectedOption;

  public GetSelectedValue() {
    this.getSelectedOption = new GetSelectedOption();
  }

  public GetSelectedValue(Command<SelenideElement> getSelectedOption) {
    this.getSelectedOption = getSelectedOption;
  }

  @Override
  @CheckReturnValue
  @Nullable
  public String execute(SelenideElement proxy, WebElementSource selectElement, @Nullable Object[] args) throws IOException {
    WebElement option = getSelectedOption.execute(proxy, selectElement, args);
    return option == null ? null : option.getAttribute("value");
  }
}
