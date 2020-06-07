package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

@ParametersAreNonnullByDefault
public class TakeScreenshotAsImage implements Command<BufferedImage> {
  @Override
  @CheckReturnValue
  @Nullable
  public BufferedImage execute(SelenideElement proxy, WebElementSource element, @Nullable Object[] args) {
    return ScreenShotLaboratory.getInstance().takeScreenshotAsImage(element.driver(), element.getWebElement());
  }
}
