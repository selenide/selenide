package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.support.ui.Select;

public class SelectOptionByValue implements Command {
  @Override
  public Object execute(SelenideElement proxy, WebElementSource selectField, Object[] args) {
    Select select = new Select(selectField.getWebElement());
    if (args[0] instanceof String) {
      select.selectByValue((String) args[0]);
    }
    else {
      String[] values = (String[]) args[0];
      for (String value : values) {
        select.selectByValue(value);
      }
    }
    return null;
  }
}
