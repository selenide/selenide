package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebElementSource;

import java.io.File;

public class TakeScreenshot implements Command<File> {
  private ScreenShotLaboratory screenshots = new ScreenShotLaboratory();

  @Override
  public File execute(SelenideElement proxy, WebElementSource element, Object[] args) {
    return screenshots.takeScreenshot(element.context(), element.getWebElement());
  }
}
