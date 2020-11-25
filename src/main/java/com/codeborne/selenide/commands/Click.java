package com.codeborne.selenide.commands;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class Click implements Command<Void> {

  @Override
  @Nullable
  public Void execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    Driver driver = locator.driver();
    WebElement webElement = locator.findAndAssertElementIsInteractable();

    if (args == null || args.length == 0) {
      click(driver, webElement);
    }
    else if (args.length == 1) {
      ClickOptions clickOptions = firstOf(args);
      click(driver, webElement, clickOptions);
    }
    else if (args.length == 2) {
      Integer offsetX = firstOf(args);
      Integer offsetY = (Integer) args[1];
      click(driver, webElement, offsetX, offsetY);
    }
    return null;
  }

  protected void click(Driver driver, WebElement element) {
    if (driver.config().clickViaJs()) {
      click(driver, element, 0, 0);
    }
    else {
      element.click();
    }
  }

  // should be removed after deleting SelenideElement.click(int offsetX, int offsetY);
  protected void click(Driver driver, WebElement element, int offsetX, int offsetY) {
    if (driver.config().clickViaJs()) {
      clickViaJS(driver, element, offsetX, offsetY);
    }
    else {
      driver.actions()
        .moveToElement(element, offsetX, offsetY)
        .click()
        .build()
        .perform();
    }
  }

  private void click(Driver driver, WebElement webElement, ClickOptions clickOptions) {
    switch (clickOptions.clickOption()) {
      case DEFAULT: {
        defaultClick(driver, webElement, clickOptions.offsetX(), clickOptions.offsetY());
        break;
      }
      case JS: {
        clickViaJS(driver, webElement, clickOptions.offsetX(), clickOptions.offsetY());
        break;
      }
      default: {
        throw new IllegalArgumentException("Unknown click option: " + clickOptions.clickOption());
      }
    }
  }

  private void defaultClick(Driver driver, WebElement element, int offsetX, int offsetY) {
      driver.actions()
        .moveToElement(element, offsetX, offsetY)
        .click()
        .perform();
  }

  private void clickViaJS(Driver driver, WebElement element, int offsetX, int offsetY) {
    driver.executeJavaScript(
      "var rect = arguments[0].getBoundingClientRect();" +
        "arguments[0].dispatchEvent(new MouseEvent('click', {" +
        " 'view': window," +
        " 'bubbles': true," +
        " 'cancelable': true," +
        " 'clientX': rect.left + rect.width/2 + arguments[1]," +
        " 'clientY': rect.top + rect.height/2 + arguments[2]" +
        "}))",
      element,
      offsetX,
      offsetY);
  }
}
