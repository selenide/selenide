package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.DownloadAction;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Supplier;

public class DownloadFileWithProxyServer {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileWithProxyServer.class);

  private final Waiter waiter;

  DownloadFileWithProxyServer(Waiter waiter) {
    this.waiter = waiter;
  }

  public DownloadFileWithProxyServer() {
    this(new Waiter());
  }

  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout,
                       FileFilter fileFilter,
                       DownloadAction action) {

    return clickAndInterceptFileByProxyServer(anyClickableElement, clickable, timeout, fileFilter, action);
  }

  private File clickAndInterceptFileByProxyServer(WebElementSource anyClickableElement, WebElement clickable,
                                                  long timeout, FileFilter fileFilter,
                                                  DownloadAction action) {
    Driver driver = anyClickableElement.driver();
    Config config = driver.config();
    if (!config.proxyEnabled()) {
      throw new IllegalStateException("Cannot download file: proxy server is not enabled. Setup proxyEnabled");
    }

    SelenideProxyServer proxyServer = driver.getProxy();

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    if (filter == null) {
      throw new IllegalStateException("Cannot download file: download filter is not activated");
    }

    filter.activate();
    try {
      long pollingInterval = Math.max(config.pollingInterval(), 50);
      waitForPreviousDownloadsCompletion(filter, timeout, pollingInterval);

      filter.reset();
      action.perform(driver, clickable);

      waitForNewDownloads(filter, fileFilter, timeout, pollingInterval);

      if (log.isInfoEnabled()) {
        log.info("Downloaded {}", filter.downloads().filesAsString());
        log.info("Just in case, intercepted {}", filter.responsesAsString());
      }
      return filter.downloads().firstDownloadedFile(timeout, fileFilter);
    }
    finally {
      filter.deactivate();
    }
  }

  private void waitForNewDownloads(FileDownloadFilter filter, FileFilter fileFilter, long timeout, long pollingInterval) {
    waiter.wait(timeout, pollingInterval, () -> !filter.downloads().files(fileFilter).isEmpty());
  }

  private void waitForPreviousDownloadsCompletion(FileDownloadFilter filter, long timeout, long pollingInterval) {
    waiter.wait(timeout, pollingInterval, new PreviousDownloadsCompleted(filter));
  }

  private static class PreviousDownloadsCompleted implements Supplier<Boolean> {
    private final FileDownloadFilter filter;
    private int downloadsCount = -1;

    PreviousDownloadsCompleted(FileDownloadFilter filter) {
      this.filter = filter;
    }

    @Override
    public Boolean get() {
      int previousCount = downloadsCount;
      downloadsCount = filter.downloads().size();
      return downloadsCount == previousCount;
    }
  }
}
