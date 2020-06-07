package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class TakeScreenshot implements Command<File> {
  @Override
  @CheckReturnValue
  @Nullable
  public File execute(SelenideElement proxy, WebElementSource element, @Nullable Object[] args) {
    return ScreenShotLaboratory.getInstance().takeScreenshot(element.driver(), element.getWebElement());
  }
}
