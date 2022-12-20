package com.codeborne.selenide.commands;

import com.codeborne.selenide.ClipboardService;
import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class Paste implements Command<SelenideElement> {
  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    WebElement input = locator.getWebElement();
    input.sendKeys(
      inject(ClipboardService.class).getClipboard(locator.driver()).getText()
    );
    return proxy;
  }
}
