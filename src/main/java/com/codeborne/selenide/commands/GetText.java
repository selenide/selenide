package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

public class GetText implements Command<String> {
  GetSelectedText getSelectedText = new GetSelectedText();
  
  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    WebElement element = locator.findAndAssertElementExists();
    return "select".equalsIgnoreCase(element.getTagName()) ? 
        getSelectedText.execute(proxy, locator, args) : 
        element.getText();
  }
}
