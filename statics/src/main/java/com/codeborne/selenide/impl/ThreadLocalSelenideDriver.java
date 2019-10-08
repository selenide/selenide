package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideDriver;

/**
 * A `SelenideDriver` implementation which uses thread-local
 * webdriver and proxy from `WebDriverRunner`.
 *
 * @see com.codeborne.selenide.impl.StaticConfig
 * @see com.codeborne.selenide.impl.StaticDriver
 */
public class ThreadLocalSelenideDriver extends SelenideDriver {
  public ThreadLocalSelenideDriver() {
    super(new StaticConfig(), new StaticDriver());
  }
}
