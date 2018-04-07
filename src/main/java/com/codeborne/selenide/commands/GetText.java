package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

public class GetText implements Command<String> {
  private GetSelectedText getSelectedText;

  public GetText() {
    this.getSelectedText = new GetSelectedText();
  }

  public GetText(GetSelectedText getSelectedtext) {
    this.getSelectedText = getSelectedtext;
  }

  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    WebElement element = locator.getWebElement();
    return "select".equalsIgnoreCase(element.getTagName()) ?
        getSelectedText.execute(proxy, locator, args) :
        element.getText();
  }
}
