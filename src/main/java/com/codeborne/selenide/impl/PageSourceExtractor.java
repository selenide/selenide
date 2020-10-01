package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.File;

public interface PageSourceExtractor {
  @Nonnull
  @CheckReturnValue
  File extract(Config config, WebDriver driver, String fileName);
}
