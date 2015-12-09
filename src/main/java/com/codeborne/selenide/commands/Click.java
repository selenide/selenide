package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Configuration.clickViaJs;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class Click implements Command<Void> {
  @Override
  public Void execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    click(locator.findAndAssertElementIsVisible());
    return null;
  }
  
  protected void click(WebElement element) {
    if (clickViaJs) {
      executeJavaScript("arguments[0].click()", element);
    } else {
      element.click();
    }
  }
}
