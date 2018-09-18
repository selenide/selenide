package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

public class SetSelected implements Command<WebElement> {
  private Click click;

  public SetSelected() {
    this.click = new Click();
  }

  public SetSelected(Click click) {
    this.click = click;
  }

  @Override
  public WebElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    boolean selected = (Boolean) args[0];
    WebElement element = locator.getWebElement();
    if (!element.isDisplayed()) {
      throw new InvalidStateException(locator.driver(), "Cannot change invisible element");
    }
    String tag = element.getTagName();
    if (!tag.equals("option")) {
      if (tag.equals("input")) {
        String type = element.getAttribute("type");
        if (!type.equals("checkbox") && !type.equals("radio")) {
          throw new InvalidStateException(locator.driver(), "Only use setSelected on checkbox/option/radio");
        }
      }
      else {
        throw new InvalidStateException(locator.driver(), "Only use setSelected on checkbox/option/radio");
      }
    }
    if (element.getAttribute("readonly") != null || element.getAttribute("disabled") != null) {
      throw new InvalidStateException(locator.driver(), "Cannot change value of readonly/disabled element");
    }
    if (element.isSelected() != selected) {
      click.execute(proxy, locator, NO_ARGS);
    }
    return proxy;
  }
}
