package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

import static java.nio.file.Files.createTempDirectory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class WebDriverWrapperTest {
  private File downloadsFolder;

  @BeforeEach
  void setUp() throws IOException {
    downloadsFolder = createTempDirectory("WebDriverWrapperTest").toFile();
  }

  @Test
  void close_closesTheBrowser() {
    WebDriver webDriver = mock(WebDriver.class);
    WebDriverWrapper driver = new WebDriverWrapper(new SelenideConfig(), webDriver, null, downloadsFolder);

    driver.close();

    verify(webDriver).quit();
    verifyNoMoreInteractions(webDriver);
    assertThat(downloadsFolder).doesNotExist();
  }

  @Test
  void close_closesTheBrowser_withProxy() {
    WebDriver webDriver = mock(WebDriver.class);
    SelenideProxyServer proxy = mock(SelenideProxyServer.class);
    WebDriverWrapper driver = new WebDriverWrapper(new SelenideConfig(), webDriver, proxy, downloadsFolder);

    driver.close();

    verify(webDriver).quit();
    verify(proxy).shutdown();
    verifyNoMoreInteractions(webDriver, proxy);
    assertThat(downloadsFolder).doesNotExist();
  }
}
