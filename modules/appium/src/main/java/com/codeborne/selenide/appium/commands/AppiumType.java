package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.TypeOptions;
import com.codeborne.selenide.appium.AppiumDriverRunner;
import com.codeborne.selenide.commands.Type;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static com.codeborne.selenide.Stopwatch.sleepAtLeast;
import static com.codeborne.selenide.appium.AppiumDriverUnwrapper.isMobile;
import static java.util.Objects.requireNonNull;

public class AppiumType extends Type {
  public AppiumType() {
    super(new AppiumClear());
  }

  @Override
  protected void execute(WebElementSource locator, Object[] args) {
    TypeOptions typeOptions = extractOptions(requireNonNull(args));
    clearField(locator, typeOptions);

    WebElement element = findElement(locator);
    typeIntoField(locator, element, typeOptions);
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
  protected WebElement findElement(WebElementSource locator) {
    return isMobile(locator.driver()) ?
      locator.findAndAssertElementIsInteractable() :
      super.findElement(locator);
  }
}
