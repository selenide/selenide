package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.support.ui.Select;

import static com.codeborne.selenide.impl.WebElementWrapper.wrap;

public class GetSelectedOption implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource selectElement, Object[] args) {
    return wrap(selectElement.driver(), new Select(selectElement.getWebElement()).getFirstSelectedOption());
  }
}
