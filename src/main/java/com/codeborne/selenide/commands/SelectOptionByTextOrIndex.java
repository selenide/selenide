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
public class SelectOptionByTextOrIndex implements Command<Void> {
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
    else if (args[0] instanceof String[]) {
      selectOptionsByTexts(selectField, (String[]) args[0]);
    }
    else if (args[0] instanceof int[]) {
      selectOptionsByIndexes(selectField, (int[]) args[0]);
    }
    return null;
  }

  private void selectOptionsByTexts(WebElementSource selectField, String[] texts) {
    WebElement selectWebElement = selectField.getWebElement();
    Select select = new Select(selectWebElement);
    for (String text : texts) {
      try {
        WebElement option = selectWebElement.findElement(By.xpath(
          ".//option[normalize-space(.) = " + Quotes.escape(text) + "]"));
        if (!option.isEnabled()) {
          throw new InvalidStateException(selectField.driver(),
            "Cannot select a disabled option: " + selectField.description() + "/option[text:" + text + "]");
        }

        select.selectByVisibleText(text);
      }
      catch (NoSuchElementException e) {
        throw new ElementNotFound(selectField.driver(),
          selectField.description() + "/option[text:" + text + ']', exist, e);
      }
    }
  }

  private void selectOptionsByIndexes(WebElementSource selectField, int[] indexes) {
    WebElement selectWebElement = selectField.getWebElement();
    Select select = new Select(selectWebElement);
    for (int index : indexes) {
      try {
        WebElement option = selectWebElement.findElement(By.xpath(
          ".//option[" + (index + 1) + "]"));
        if (!option.isEnabled()) {
          throw new InvalidStateException(selectField.driver(),
            "Cannot select a disabled option: " + selectField.description() + "/option[index:" + index + "]");
        }

        select.selectByIndex(index);
      }
      catch (NoSuchElementException e) {
        throw new ElementNotFound(selectField.driver(),
          selectField.description() + "/option[index:" + index + ']', exist, e);
      }
    }
  }
}
