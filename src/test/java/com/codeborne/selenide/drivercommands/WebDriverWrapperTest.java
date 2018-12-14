package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class WebDriverWrapperTest {

  @Test
  void name() {
    WebDriver webDriver = mock(WebDriver.class);
    WebDriverWrapper driver = new WebDriverWrapper(new SelenideConfig(), webDriver, null);

    driver.close();

    verifyNoMoreInteractions(webDriver);
  }
}
