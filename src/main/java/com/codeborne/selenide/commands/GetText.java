package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

public class GetText implements Command<String> {
  private final GetSelectedOptionText getSelectedOptionText;

  public GetText() {
    this(new GetSelectedOptionText());
  }

  GetText(GetSelectedOptionText getSelectedOptionText) {
    this.getSelectedOptionText = getSelectedOptionText;
  }

  @Override
  public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    WebElement element = locator.getWebElement();
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionText.execute(proxy, locator, args) :
      element.getText();
  }
}
