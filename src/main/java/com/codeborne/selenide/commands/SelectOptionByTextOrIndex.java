package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.support.ui.Select;

public class SelectOptionByTextOrIndex implements Command<Void> {
  @Override
  public Void execute(SelenideElement proxy, WebElementSource selectField, Object[] args) {
    Select select = new Select(selectField.getWebElement());
    if (args[0] instanceof String) {
      select.selectByVisibleText((String) args[0]);
    }
    else {
      select.selectByIndex((Integer) args[0]);
    }
    return null;
  }
}
