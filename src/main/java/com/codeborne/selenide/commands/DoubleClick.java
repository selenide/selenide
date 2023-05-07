package com.codeborne.selenide.commands;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DoubleClick extends Click {

  private final JavaScript jsSource = new JavaScript("dblclick.js");

  @Override
  protected void clickViaJS(Driver driver, WebElement element, int offsetX, int offsetY) {
    jsSource.execute(driver, element, offsetX, offsetY);
  }

  @Override
  protected void defaultClick(Driver driver, WebElement element) {
    driver.actions()
      .doubleClick(element)
      .build().perform();
  }

  @Override
  protected void defaultClick(Driver driver, WebElement element, int offsetX, int offsetY) {
    driver.actions()
      .moveToElement(element, offsetX, offsetY)
      .doubleClick()
      .build().perform();
  }
}
