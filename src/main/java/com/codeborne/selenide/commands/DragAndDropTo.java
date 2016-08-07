package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.actions;

public class DragAndDropTo implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    SelenideElement target;
    if (args[0] instanceof String) {
      target = $((String) args[0]);
    }
    else if (args[0] instanceof WebElement) {
      target = $((WebElement) args[0]);
    }
    else {
      throw new IllegalArgumentException("Unknown target type: " + args[0] + 
          " (only String or WebElement are supported)");
    }
    target.shouldBe(visible);
    actions().dragAndDrop(locator.getWebElement(), target).perform();
    return proxy;
  }
}
