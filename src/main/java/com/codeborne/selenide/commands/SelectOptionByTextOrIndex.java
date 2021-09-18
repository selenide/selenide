package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.exist;

@ParametersAreNonnullByDefault
public class SelectOptionByTextOrIndex implements Command<Void> {
  @Override
  @Nullable
  public Void execute(SelenideElement proxy, WebElementSource selectField, @Nullable Object[] args) {
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Missing arguments");
    }
    else if (args[0] instanceof String[]) {
      selectOptionsByTexts(selectField, (String[]) args[0]);
    }
    else if (args[0] instanceof int[]) {
      selectOptionsByIndexes(selectField, (int[]) args[0]);
    }
    return null;
  }

  private void selectOptionsByTexts(WebElementSource selectField, String[] texts) {
    Select select = new Select(selectField.getWebElement());
    for (String text : texts) {
      try {
        select.selectByVisibleText(text);
      }
      catch (NoSuchElementException e) {
        throw new ElementNotFound(selectField.driver(), selectField.description() + "/option[text:" + text + ']', exist, e);
      }
    }
  }

  private void selectOptionsByIndexes(WebElementSource selectField, int[] indexes) {
    Select select = new Select(selectField.getWebElement());
    for (int index : indexes) {
      try {
        select.selectByIndex(index);
      }
      catch (NoSuchElementException e) {
        throw new ElementNotFound(selectField.driver(), selectField.description() + "/option[index:" + index + ']', exist, e);
      }
    }
  }
}
