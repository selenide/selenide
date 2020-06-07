package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GetText implements Command<String> {
  private final GetSelectedText getSelectedText;

  public GetText() {
    this.getSelectedText = new GetSelectedText();
  }

  public GetText(GetSelectedText getSelectedtext) {
    this.getSelectedText = getSelectedtext;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    WebElement element = locator.getWebElement();
    return "select".equalsIgnoreCase(element.getTagName()) ?
        getSelectedText.execute(proxy, locator, args) :
        element.getText();
  }
}
