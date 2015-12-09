package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.impl.WebElementWrapper.wrap;

public class SelectRadio implements Command<SelenideElement> {
  Click click = new Click();
  
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String value = (String) args[0];
    List<WebElement> matchingRadioButtons = locator.findAll();
    for (WebElement radio : matchingRadioButtons) {
      if (value.equals(radio.getAttribute("value"))) {
        if (radio.getAttribute("readonly") != null)
          throw new InvalidStateException("Cannot select readonly radio button");
        click.click(radio);
        return wrap(radio);
      }
    }
    throw new ElementNotFound(locator.getSearchCriteria(), value(value));
  }
}
