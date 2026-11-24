package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.impl.WebDriverInstance;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

final class CloseDriverCommandTest {
  private final CloseDriverCommand command = new CloseDriverCommand();
  private final WebDriver webDriver = mock();
  private final DownloadsFolder downloadsFolder = mock();

  @Test
  void deletesDownloadsFolder_afterClosingBrowser() {
    command.close(instance(new SelenideConfig()));

    InOrder inOrder = inOrder(webDriver, downloadsFolder);
    inOrder.verify(webDriver).quit();
    inOrder.verify(downloadsFolder).deleteIfEmpty();
  }

  @Test
  void deletesDownloadsFolder_beforeShuttingDownProxy() {
    SelenideProxyServer proxy = mock();
    when(proxy.isStarted()).thenReturn(true);
    SelenideConfig config = new SelenideConfig().proxyEnabled(true);

    command.close(new WebDriverInstance(-1L, config, webDriver, proxy, downloadsFolder, emptyList(), "test"));

    InOrder inOrder = inOrder(webDriver, downloadsFolder, proxy);
    inOrder.verify(webDriver).quit();
    inOrder.verify(downloadsFolder).deleteIfEmpty();
    inOrder.verify(proxy).shutdown();
  }

  @Test
  void deletesDownloadsFolder_evenIfFailedToCloseBrowser() {
    doThrow(new WebDriverException("browser is dead")).when(webDriver).quit();

    command.close(instance(new SelenideConfig()));

    verify(downloadsFolder).deleteIfEmpty();
  }

  @Test
  void doesNotCloseAnything_ifHoldBrowserOpen() {
    command.close(instance(new SelenideConfig().holdBrowserOpen(true)));

    verifyNoInteractions(webDriver, downloadsFolder);
  }

  private WebDriverInstance instance(SelenideConfig config) {
    return new WebDriverInstance(-1L, config, webDriver, null, downloadsFolder, emptyList(), "test");
  }
}
