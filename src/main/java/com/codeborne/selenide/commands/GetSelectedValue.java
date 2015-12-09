package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public class GetSelectedValue implements Command<String> {
  private Command<SelenideElement> getSelectedOption = new GetSelectedOption();
  
  @Override
  public String execute(SelenideElement proxy, WebElementSource selectElement, Object[] args) throws IOException {
    WebElement option = getSelectedOption.execute(proxy, selectElement, args);
    return option == null ? null : option.getAttribute("value");
  }
}
