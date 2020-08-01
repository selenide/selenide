package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class SelenideDriverFinalCleanupThreadTest {
  @Test
  void closesDriverAndProxy() {
    Config config = mock(Config.class);
    WebDriver driver = mock(WebDriver.class);
    SelenideProxyServer proxy = mock(SelenideProxyServer.class);
    File browserDownloadsFolder = mock(File.class);
    CloseDriverCommand closeDriverCommand = mock(CloseDriverCommand.class);

    new SelenideDriverFinalCleanupThread(config, driver, proxy, browserDownloadsFolder, closeDriverCommand).run();

    verify(closeDriverCommand).closeAsync(config, driver, proxy, browserDownloadsFolder);
    verifyNoMoreInteractions(closeDriverCommand);
    verifyNoInteractions(config);
    verifyNoInteractions(driver);
    verifyNoInteractions(proxy);
  }
}
