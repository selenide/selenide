package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;
import org.openqa.selenium.support.ui.Select;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.exist;

@ParametersAreNonnullByDefault
public class SelectOptionByValue implements Command<Void> {
  @Override
  @Nullable
  public Void execute(SelenideElement proxy, WebElementSource selectField, @Nullable Object[] args) {
    if (!selectField.getWebElement().isEnabled()) {
      throw new InvalidStateException(selectField.driver(),
        "Cannot select anything in a disabled select element: " + selectField);
    }

    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Missing arguments");
    }
    else if (args[0] instanceof String) {
      selectOptionByValue(selectField, (String) args[0]);
    }
    else if (args[0] instanceof String[]) {
      String[] values = (String[]) args[0];
      for (String value : values) {
        selectOptionByValue(selectField, value);
      }
    }
    return null;
  }

  private void selectOptionByValue(WebElementSource selectField, String value) {
    Select select = new Select(selectField.getWebElement());
    try {
      select.selectByValue(value);

      WebElement option = selectField.getWebElement().findElement(By.xpath(
        ".//option[@value = " + Quotes.escape(value) + "]"));
      if (!option.isEnabled()) {
        throw new InvalidStateException(selectField.driver(),
          "Cannot select a disabled option: " + selectField.description() + "/option[value:" + value + "]");
      }
    }
    catch (NoSuchElementException e) {
      throw new ElementNotFound(selectField.driver(),
        selectField.description() + "/option[value:" + value + ']', exist, e);
    }
  }
}
