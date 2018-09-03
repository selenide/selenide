package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebElementSource;

import java.awt.image.BufferedImage;

public class TakeScreenshotAsImage implements Command<BufferedImage> {
  private final ScreenShotLaboratory screenshots = new ScreenShotLaboratory();

  @Override
  public BufferedImage execute(SelenideElement proxy, WebElementSource element, Object[] args) {
    return screenshots.takeScreenshotAsImage(element.context(), element.getWebElement());
  }
}
