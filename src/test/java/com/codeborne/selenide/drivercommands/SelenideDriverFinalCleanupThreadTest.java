package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class SelenideDriverFinalCleanupThreadTest {
  @Test
  void closesDriverAndProxy() {
    WebDriver driver = mock(WebDriver.class);
    SelenideProxyServer proxy = mock(SelenideProxyServer.class);
    CloseDriverCommand closeDriverCommand = mock(CloseDriverCommand.class);

    new SelenideDriverFinalCleanupThread(driver, proxy, closeDriverCommand).start();

    verify(closeDriverCommand).closeAsync(driver, proxy);
    verifyNoMoreInteractions(closeDriverCommand);
    verifyNoInteractions(driver);
    verifyNoInteractions(proxy);
  }
}
