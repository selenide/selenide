package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.TypeOptions;
import com.codeborne.selenide.appium.AppiumDriverRunner;
import com.codeborne.selenide.commands.Type;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Stopwatch.sleepAtLeast;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class AppiumType extends Type {
  public AppiumType() {
    super(new AppiumClear());
  }

  @Nullable
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    TypeOptions typeOptions = extractOptions(requireNonNull(args));
    clearField(proxy, locator, typeOptions);

    WebElement element = findElement(locator);
    typeIntoField(locator, element, typeOptions);
    return proxy;
  }

  private void typeIntoField(WebElementSource locator, WebElement element, TypeOptions typeOptions) {
    Actions actions = new Actions(locator.driver().getWebDriver());
    for (int i = 0; i < typeOptions.textToType().length(); i++) {
      CharSequence character = typeOptions.textToType().subSequence(i, i + 1);
      if (AppiumDriverRunner.isAndroidDriver()) {
        element.click();
        actions.sendKeys(character).perform();
      } else {
        element.sendKeys(character);
      }
      sleepAtLeast(typeOptions.timeDelay().toMillis());
    }
  }

  @Override
  @Nonnull
  @CheckReturnValue
  protected WebElement findElement(WebElementSource locator) {
    return isMobile(locator.driver()) ?
      locator.findAndAssertElementIsInteractable() :
      super.findElement(locator);
  }
}
