package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.HoverOptions;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class Hover implements Command<SelenideElement> {
  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
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
    return proxy;
  }
}
