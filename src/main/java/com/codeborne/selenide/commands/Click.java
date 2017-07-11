package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Configuration.clickViaJs;
import static com.codeborne.selenide.Selenide.actions;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class Click implements Command<Void> {
  @Override
  public Void execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    if (args == null || args.length == 0) {
      click(locator.findAndAssertElementIsVisible());
    }
    else if (args.length == 2) {
      click(locator.findAndAssertElementIsVisible(), (int) args[0], (int) args[1]);
    }
    return null;
  }
  
  protected void click(WebElement element) {
    if (clickViaJs) {
      executeJavaScript("arguments[0].click()", element);
    } else {
      element.click();
    }
  }

  protected void click(WebElement element, int offsetX, int offsetY) {
    if (clickViaJs) {
      executeJavaScript("arguments[0].dispatchEvent(new MouseEvent('click', { 'view': window, 'bubbles': true, 'cancelable': true, 'offsetX': this.target.left + arguments[1], 'offsetY': this.target.top + arguments[2] }))", element, offsetX, offsetY);
    } else {
      actions().moveToElement(element, offsetX, offsetY).click().build().perform();
    }
  }
}
