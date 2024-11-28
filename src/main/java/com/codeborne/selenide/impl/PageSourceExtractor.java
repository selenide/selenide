package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import org.openqa.selenium.WebDriver;

import java.io.File;

@FunctionalInterface
public interface PageSourceExtractor {
  File extract(Config config, WebDriver driver, String fileName);
}
