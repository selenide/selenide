package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.support.ui.Select;

public class SelectOptionByText implements Command<Void> {
  @Override
  public Void execute(SelenideElement proxy, WebElementSource selectField, Object[] args) {
    new Select(selectField.getWebElement()).selectByVisibleText((String) args[0]);
    return null;
  }
}
