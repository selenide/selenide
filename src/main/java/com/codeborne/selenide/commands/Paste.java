package com.codeborne.selenide.commands;

import com.codeborne.selenide.ClipboardService;
import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.impl.Plugins.inject;

public class Paste extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    WebElement input = locator.getWebElement();
    input.sendKeys(
      inject(ClipboardService.class).getClipboard(locator.driver()).getText()
    );
  }
}
