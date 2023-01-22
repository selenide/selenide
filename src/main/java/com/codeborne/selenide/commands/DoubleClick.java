package com.codeborne.selenide.commands;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DoubleClick extends Click {

  private final JavaScript jsSource = new JavaScript("dblclick.js");

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    return super.execute(proxy, locator, args);
  }

  @Override
  protected void clickViaJS(Driver driver, WebElement element, int offsetX, int offsetY) {
    jsSource.execute(driver, element, offsetX, offsetY);
  }

  @Override
  protected void defaultClick(WebElement element, Driver driver) {
    driver.actions().doubleClick(element).build().perform();
  }

  @Override
  protected void defaultClick(Driver driver, WebElement element, int offsetX, int offsetY) {
    driver.actions().moveByOffset(offsetX, offsetY).doubleClick(element).build().perform();
  }
}
