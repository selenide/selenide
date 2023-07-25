package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.DoubleClick;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static com.codeborne.selenide.ClickMethod.JS;
import static com.codeborne.selenide.appium.WebdriverUnwrapper.isMobile;
import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class AppiumDoubleClick implements Command<SelenideElement> {

  private final DoubleClick defaultDoubleClick = new DoubleClick();

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) throws IOException {
    if (!isMobile(locator.driver())) {
      return defaultDoubleClick.execute(proxy, locator, args);
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
    return proxy;
  }

  private ClickOptions options(@Nullable Object[] args) {
    return (args == null || args.length == 0) ?
      ClickOptions.usingDefaultMethod() :
      firstOf(args);
  }

  @Nonnull
  @CheckReturnValue
  protected WebElement findElement(WebElementSource locator) {
    return locator.getWebElement();
  }
}
