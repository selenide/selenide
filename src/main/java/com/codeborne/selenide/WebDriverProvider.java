package com.codeborne.selenide;

import org.openqa.selenium.WebDriver;

public interface WebDriverProvider {
  WebDriver createDriver();
}
