package com.codeborne.selenide.commands;

import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.HoverOptions;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static com.codeborne.selenide.commands.Util.firstOf;

public class Hover extends FluentCommand {
  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    WebElement element = locator.getWebElement();
    Actions actions = new Actions(locator.driver().getWebDriver());

    if (args != null && args.length == 1) {
      HoverOptions options = firstOf(args);
      actions.moveToElement(element, options.offsetX(), options.offsetY());
    }
    else {
      actions.moveToElement(element);
    }

    actions.perform();
  }
}
