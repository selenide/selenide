package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

import static com.codeborne.selenide.Condition.exist;

public class SelectOptionByValue implements Command {
  @Override
  public Object execute(SelenideElement proxy, WebElementSource selectField, Object[] args) {
    Select select = new Select(selectField.getWebElement());

    if (args[0] instanceof String) {
      selectOptionByValue(selectField, select, (String) args[0]);
    }
    else if (args[0] instanceof String[]) {
      String[] values = (String[]) args[0];
      for (String value : values) {
        selectOptionByValue(selectField, select, value);
      }
    }
    return null;
  }

  private void selectOptionByValue(WebElementSource selectField, Select select, String value) {
    try {
      select.selectByValue(value);
    }
    catch (NoSuchElementException e) {
      throw new ElementNotFound(selectField.driver(), selectField.getSearchCriteria() + "/option[value:" + value + ']', exist, e);
    }
  }
}
