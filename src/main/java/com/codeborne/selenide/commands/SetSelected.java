package com.codeborne.selenide.commands;

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
      if (element.getAttribute("readonly") != null) {
        throw new InvalidStateException("Cannot change value of readonly element");
      }
      if (selected && !isSelectable(element)) {
        throw new InvalidStateException("Can select option element or input of type checkbox or radio element only.");
      }
      if (!selected && !isDeselectable(element)) {
        throw new InvalidStateException("Can de-select input of type checkbox element only.");
      }
      click.execute(proxy, locator, NO_ARGS);
    }
    return proxy;
  }

  private boolean isSelectable(WebElement element) {
    String tagName = element.getTagName();
    if ("option".equals(tagName)) {
      return true;
    }
    if ("input".equals(tagName)) {
      String type = element.getAttribute("type");
      return "radio".equals(type) || "checkbox".equals(type);
    }
    return false;
  }

  private boolean isDeselectable(WebElement element) {
    String tagName = element.getTagName();
    if ("input".equals(tagName)) {
      String type = element.getAttribute("type");
      return "checkbox".equals(type);
    }
    return false;
  }
}
