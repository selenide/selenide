package com.codeborne.selenide.commands;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class Click implements Command<Void> {
  private final JavaScript jsSource = new JavaScript("click.js");

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
    else {
      throw new IllegalArgumentException("Unsupported click arguments: " + Arrays.toString(args));
    }
    return null;
  }

  protected void click(Driver driver, WebElement element) {
    if (driver.config().clickViaJs()) {
      clickViaJS(driver, element, 0, 0);
    }
    else {
      element.click();
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
    jsSource.execute(driver, element, offsetX, offsetY);
  }
}
