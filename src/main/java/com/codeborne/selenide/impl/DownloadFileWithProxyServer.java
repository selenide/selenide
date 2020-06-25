package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class DownloadFileWithProxyServer {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileWithProxyServer.class);

  private final Waiter waiter;
  private final WindowsCloser windowsCloser;

  DownloadFileWithProxyServer(Waiter waiter, WindowsCloser windowsCloser) {
    this.waiter = waiter;
    this.windowsCloser = windowsCloser;
  }

  public DownloadFileWithProxyServer() {
    this(new Waiter(), new WindowsCloser());
  }

  @CheckReturnValue
  @Nonnull
  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout,
                       FileFilter fileFilter) throws FileNotFoundException {

    WebDriver webDriver = anyClickableElement.driver().getWebDriver();
    return windowsCloser.runAndCloseArisedWindows(webDriver, () ->
      clickAndInterceptFileByProxyServer(anyClickableElement, clickable, timeout, fileFilter)
    );
  }

  @Nonnull
  private File clickAndInterceptFileByProxyServer(WebElementSource anyClickableElement, WebElement clickable,
                                                  long timeout, FileFilter fileFilter) throws FileNotFoundException {
    Config config = anyClickableElement.driver().config();
    if (!config.proxyEnabled()) {
      throw new IllegalStateException("Cannot download file: proxy server is not enabled. Setup proxyEnabled");
    }

    SelenideProxyServer proxyServer = anyClickableElement.driver().getProxy();
    if (proxyServer == null) {
      throw new IllegalStateException("Cannot download file: proxy server is not started");
    }

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    if (filter == null) {
      throw new IllegalStateException("Cannot download file: download filter is not activated");
    }

    filter.activate();
    try {
      waiter.wait(filter, new PreviousDownloadsCompleted(), timeout, config.pollingInterval());

      filter.reset();
      clickable.click();

      waiter.wait(filter, new HasDownloads(fileFilter), timeout, config.pollingInterval());

      if (log.isInfoEnabled()) {
        log.info(filter.downloads().filesAsString());
        log.info("Just in case, all intercepted responses: {}", filter.responsesAsString());
      }
      return filter.downloads().firstDownloadedFile(anyClickableElement.toString(), timeout, fileFilter);
    }
    finally {
      filter.deactivate();
    }
  }

  @ParametersAreNonnullByDefault
  private static class HasDownloads implements Predicate<FileDownloadFilter> {
    private final FileFilter fileFilter;

    private HasDownloads(FileFilter fileFilter) {
      this.fileFilter = fileFilter;
    }

    @Override
    public boolean test(FileDownloadFilter filter) {
      return !filter.downloads().files(fileFilter).isEmpty();
    }
  }

  @ParametersAreNonnullByDefault
  private static class PreviousDownloadsCompleted implements Predicate<FileDownloadFilter> {
    private int downloadsCount = -1;

    @Override
    public boolean test(FileDownloadFilter filter) {
      try {
        return downloadsCount == filter.downloads().size();
      }
      finally {
        downloadsCount = filter.downloads().size();
      }
    }
  }
}
