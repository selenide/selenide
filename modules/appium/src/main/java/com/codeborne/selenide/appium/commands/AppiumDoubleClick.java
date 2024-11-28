package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.FluentCommand;
import com.codeborne.selenide.commands.DoubleClick;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ClickMethod.JS;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static com.codeborne.selenide.commands.Util.firstOf;

public class AppiumDoubleClick extends FluentCommand {

  private final DoubleClick defaultDoubleClick = new DoubleClick();

  @Override
  protected void execute(WebElementSource locator, Object @Nullable [] args) {
    if (!isMobile(locator.driver())) {
      defaultDoubleClick.execute(locator, args);
      return;
    }
    Driver driver = locator.driver();
    WebElement webElement = findElement(locator);
    ClickOptions clickOptions = options(args);
    if (clickOptions.clickMethod() == JS) {
      throw new UnsupportedOperationException("Click using JavaScript is not supported in mobile");
    }
    if (clickOptions.timeout() != null) {
      throw new UnsupportedOperationException("Click timeout is not supported in mobile");
    }
    else {
      driver.actions().doubleClick(webElement);
    }
  }

  private ClickOptions options(Object @Nullable [] args) {
    return (args == null || args.length == 0) ?
      ClickOptions.usingDefaultMethod() :
      firstOf(args);
  }

  protected WebElement findElement(WebElementSource locator) {
    return locator.getWebElement();
  }
}
