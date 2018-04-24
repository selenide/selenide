package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

public class GetSelectedText implements Command<String> {
  private GetSelectedOption getSelectedOption;

  public GetSelectedText(GetSelectedOption getSelectedOption) {
    this.getSelectedOption = getSelectedOption;
  }

  public GetSelectedText() {
    this.getSelectedOption = new GetSelectedOption();
  }

  @Override
  public String execute(SelenideElement proxy, WebElementSource selectElement, Object[] args) {
    WebElement option = getSelectedOption.execute(proxy, selectElement, NO_ARGS);
    return option == null ? null : option.getText();
  }
}
