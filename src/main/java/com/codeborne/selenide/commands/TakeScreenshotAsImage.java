package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import java.awt.image.BufferedImage;

public class TakeScreenshotAsImage implements Command<BufferedImage> {
  @Override
  public BufferedImage execute(SelenideElement proxy, WebElementSource element, Object @Nullable [] args) {
    return ScreenShotLaboratory.getInstance().takeScreenshotAsImage(element.driver(), element.getWebElement());
  }
}
