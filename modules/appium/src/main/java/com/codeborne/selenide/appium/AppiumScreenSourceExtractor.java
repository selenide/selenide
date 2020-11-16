package com.codeborne.selenide.appium;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.WebPageSourceExtractor;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class AppiumScreenSourceExtractor extends WebPageSourceExtractor {
  @CheckReturnValue
  @Nonnull
  @Override
  protected File createFile(Config config, String fileName) {
    return new File(config.reportsFolder(), fileName + ".xml");
  }
}
