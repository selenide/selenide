package com.codeborne.selenide;

import org.openqa.selenium.WebDriver;

/**
 * We found that this interface is useless.
 * It's easier to call method com.codeborne.selenide.WebDriverRunner#setWebDriver(org.openqa.selenium.WebDriver).
 *
 * Another alternative is overriding com.codeborne.selenide.impl.WebDriverThreadLocalContainer
 *
 * @deprecated
 */
@Deprecated
public interface WebDriverProvider {
  @Deprecated
  WebDriver createDriver();
}
