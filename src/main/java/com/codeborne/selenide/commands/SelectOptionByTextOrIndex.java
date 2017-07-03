package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

public class SelectOptionByTextOrIndex implements Command<Void> {
  @Override
  public Void execute(SelenideElement proxy, WebElementSource selectField, Object[] args) {
    Select select = new Select(selectField.getWebElement());
    if (args[0] instanceof String[]) {
      selectOptionsByTexts(select, (String[]) args[0]);
    }
    else {
      selectOptionsByIndexes(select, (int[]) args[0]);
    }
    return null;
  }

  private void selectOptionsByTexts(Select select, String[] texts) {
    for (String text : texts) {
      try {
        select.selectByVisibleText(text);
      } catch (NoSuchElementException e) {
        throw new ElementNotFound(text, Condition.exist, e);
      }
    }
  }

  private void selectOptionsByIndexes(Select select, int[] indexes) {
    for (Integer index : indexes) {
      try {
        select.selectByIndex(index);
      } catch (NoSuchElementException e) {
        throw new ElementNotFound(String.valueOf(index), Condition.exist, e);
      }
    }
  }
}
