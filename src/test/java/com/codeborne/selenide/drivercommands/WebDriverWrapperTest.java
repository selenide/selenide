package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class WebDriverWrapperTest {

  @Test
  void close_closesTheBrowser() {
    WebDriver webDriver = mock(WebDriver.class);
    WebDriverWrapper driver = new WebDriverWrapper(new SelenideConfig(), webDriver, null);

    driver.close();

    verify(webDriver).quit();
    verifyNoMoreInteractions(webDriver);
  }
}
