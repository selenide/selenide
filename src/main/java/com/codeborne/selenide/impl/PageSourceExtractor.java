package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;

import java.io.File;

@FunctionalInterface
public interface PageSourceExtractor {
  @Nullable
  File extract(Config config, WebDriver driver, String fileName);
}
