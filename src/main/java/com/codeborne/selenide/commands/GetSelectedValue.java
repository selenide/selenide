package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public class GetSelectedValue implements Command<String> {
  private Command<SelenideElement> getSelectedOption;

  public GetSelectedValue() {
    this.getSelectedOption = new GetSelectedOption();
  }

  public GetSelectedValue(Command<SelenideElement> getSelectedOption) {
    this.getSelectedOption = getSelectedOption;
  }

  @Override
  public String execute(SelenideElement proxy, WebElementSource selectElement, Object[] args) throws IOException {
    WebElement option = getSelectedOption.execute(proxy, selectElement, args);
    return option == null ? null : option.getAttribute("value");
  }
}
