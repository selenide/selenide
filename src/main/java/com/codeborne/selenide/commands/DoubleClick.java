package com.codeborne.selenide.commands;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

public class DoubleClick extends Click {

  private final JavaScript jsSource = new JavaScript("dblclick.js");

  @Override
  protected void clickViaJS(Driver driver, WebElement element, int offsetX, int offsetY, List<Keys> holdingKeys) {
    jsSource.execute(driver, element, offsetX, offsetY, toClickEventOptions(holdingKeys));
  }

  @Override
  protected void defaultClick(Driver driver, WebElement element, int offsetX, int offsetY, List<Keys> holdingKeys) {
    Actions actions = driver.actions();
    if (isCenter(offsetX, offsetY)) {
      holdKeys(actions, holdingKeys);
      actions.doubleClick(element);
    }
    else {
      actions.moveToElement(element, offsetX, offsetY);
      holdKeys(actions, holdingKeys);
      actions.doubleClick();
    }

    releaseKeys(actions, holdingKeys);
    actions.build().perform();
  }
}
