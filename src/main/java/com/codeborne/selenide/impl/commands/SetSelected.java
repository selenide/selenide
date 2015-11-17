package com.codeborne.selenide.impl.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

public class SetSelected implements Command<WebElement> {
  Click click = new Click();
  
  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    boolean selected = (Boolean) args[0];
    WebElement element = locator.getWebElement();
    if (element.isSelected() ^ selected) {
      if (element.getAttribute("readonly") != null)
        throw new InvalidStateException("Cannot change value of readonly element");
      click.execute(proxy, locator, NO_ARGS);
    }
    return proxy;
  }
}
