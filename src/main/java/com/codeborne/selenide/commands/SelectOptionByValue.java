package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.support.ui.Select;

public class SelectOptionByValue implements Command {
  @Override
  public Object execute(SelenideElement proxy, WebElementSource selectField, Object[] args) {
    new Select(selectField.getWebElement()).selectByValue((String) args[0]);
    return null;
  }
}
