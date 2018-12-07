package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

public class Click implements Command<Void> {
  @Override
  public Void execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    if (args == null || args.length == 0) {
      click(locator.driver(), locator.findAndAssertElementIsInteractable());
    }
    else if (args.length == 2) {
      click(locator.driver(), locator.findAndAssertElementIsInteractable(), (int) args[0], (int) args[1]);
    }
    return null;
  }

  protected void click(Driver driver, WebElement element) {
    if (driver.config().clickViaJs()) {
      driver.executeJavaScript("arguments[0].click()", element);
    }
    else {
      element.click();
    }
  }

  protected void click(Driver driver, WebElement element, int offsetX, int offsetY) {
    if (driver.config().clickViaJs()) {
      driver.executeJavaScript("arguments[0].dispatchEvent(new MouseEvent('click', {" +
          "'view': window," +
          "'bubbles': true," +
          "'cancelable': true," +
          "'clientX': arguments[0].getClientRects()[0].left + arguments[1]," +
          "'clientY': arguments[0].getClientRects()[0].top + arguments[2]" +
          "}))",
        element,
        offsetX,
        offsetY);
    }
    else {
      driver.actions()
        .moveToElement(element, offsetX, offsetY)
        .click()
        .build()
        .perform();
    }
  }
}
