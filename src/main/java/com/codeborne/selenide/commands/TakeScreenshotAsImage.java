package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.impl.Plugins.inject;

public class TakeScreenshotAsImage implements Command<BufferedImage> {
  private static final ScreenShotLaboratory screenshots = inject();

  @Override
  public BufferedImage execute(SelenideElement proxy, WebElementSource element, Object @Nullable [] args) {
    return screenshots.takeScreenshotAsImage(element.driver(), element.getWebElement());
  }
}
