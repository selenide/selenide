package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Context;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Configuration.clickViaJs;

public class Click implements Command<Void> {
  @Override
  public Void execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    if (args == null || args.length == 0) {
      click(locator.context(), locator.findAndAssertElementIsVisible());
    }
    else if (args.length == 2) {
      click(locator.context(), locator.findAndAssertElementIsVisible(), (int) args[0], (int) args[1]);
    }
    return null;
  }

  protected void click(Context context, WebElement element) {
    if (clickViaJs) {
      context.executeJavaScript("arguments[0].click()", element);
    }
    else {
      element.click();
    }
  }

  protected void click(Context context, WebElement element, int offsetX, int offsetY) {
    if (clickViaJs) {
      context.executeJavaScript("arguments[0].dispatchEvent(new MouseEvent('click', {" +
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
      context.actions()
          .moveToElement(element, offsetX, offsetY)
          .click()
          .build()
          .perform();
    }
  }
}
