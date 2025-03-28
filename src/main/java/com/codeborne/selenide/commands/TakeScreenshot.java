package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import java.io.File;

import static com.codeborne.selenide.impl.Plugins.inject;

public class TakeScreenshot implements Command<File> {
  private static final ScreenShotLaboratory screenshots = inject();

  @Override
  public File execute(SelenideElement proxy, WebElementSource element, Object @Nullable [] args) {
    return screenshots.takeScreenshot(element.driver(), element.getWebElement());
  }
}
