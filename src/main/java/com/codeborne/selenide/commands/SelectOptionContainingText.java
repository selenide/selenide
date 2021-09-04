package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;
import org.openqa.selenium.support.ui.Select;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class SelectOptionContainingText implements Command<Void> {
  @Override
  @Nullable
  public Void execute(SelenideElement proxy, WebElementSource selectField, @Nullable Object[] args) {
    String text = firstOf(args);
    WebElement element = selectField.getWebElement();
    Select select = new Select(element);

    List<WebElement> options = element.findElements(By.xpath(
        ".//option[contains(normalize-space(.), " + Quotes.escape(text) + ")]"));

    if (options.isEmpty()) {
      throw new NoSuchElementException("Cannot locate option containing text: " + text);
    }
    if (!element.isEnabled()) {
      throw new InvalidStateException(selectField.driver(),
        "Cannot select anything in a disabled select element: " + selectField);
    }

    for (WebElement option : options) {
      if (!option.isEnabled()) {
        throw new InvalidStateException(selectField.driver(),
          "Cannot select a disabled option containing text: " + text);
      }

      setSelected(option);
      if (!select.isMultiple()) {
        break;
      }
    }

    return null;
  }

  private void setSelected(WebElement option) {
    if (!option.isSelected()) {
      option.click();
    }
  }
}
