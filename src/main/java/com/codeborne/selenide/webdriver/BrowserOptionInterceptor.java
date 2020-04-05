package com.codeborne.selenide.webdriver;

import org.openqa.selenium.MutableCapabilities;

public interface BrowserOptionInterceptor<T extends MutableCapabilities> {
  void afterSelenideChangesOptions(T options);
}
