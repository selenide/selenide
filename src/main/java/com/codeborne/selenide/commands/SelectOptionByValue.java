package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

public class SelectOptionByValue implements Command {
  @Override
  public Object execute(SelenideElement proxy, WebElementSource selectField, Object[] args) {
    Select select = new Select(selectField.getWebElement());
    if (args[0] instanceof String) {
      try {
        select.selectByValue((String) args[0]);
      } catch (NoSuchElementException e) {
        throw new ElementNotFound("Cannot locate option with value: @" + (String) args[0], Condition.exist, e);
      }
    } else if (args[0] instanceof String[]) {
      String[] values = (String[]) args[0];
      for (String value : values) {
        try {
          select.selectByValue(value);
        } catch (NoSuchElementException e) {
          throw new ElementNotFound("Cannot locate option with value: @" + value, Condition.exist, e);
        }
      }
    }
    return null;
  }
}
