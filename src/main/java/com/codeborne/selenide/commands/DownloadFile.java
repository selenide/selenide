package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.files.FileFilters;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.FileDownloadMode.HTTPGET;

@ParametersAreNonnullByDefault
public class DownloadFile implements Command<File> {
  private static final Logger log = LoggerFactory.getLogger(DownloadFile.class);

  private final DownloadFileWithHttpRequest downloadFileWithHttpRequest;
  private final DownloadFileWithProxyServer downloadFileWithProxyServer;

  public DownloadFile() {
    this(new DownloadFileWithHttpRequest(), new DownloadFileWithProxyServer());
  }

  DownloadFile(DownloadFileWithHttpRequest httpget, DownloadFileWithProxyServer proxy) {
    downloadFileWithHttpRequest = httpget;
    downloadFileWithProxyServer = proxy;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public File execute(SelenideElement selenideElement, WebElementSource linkWithHref, @Nullable Object[] args) throws IOException {
    WebElement link = linkWithHref.findAndAssertElementIsInteractable();
    Config config = linkWithHref.driver().config();

    long timeout = getTimeout(config, args);
    FileFilter fileFilter = getFileFilter(args);

    switch (config.fileDownload()) {
      case HTTPGET: {
        log.debug("selenide.fileDownload = {} download file via http get", config.fileDownload());
        return downloadFileWithHttpRequest.download(linkWithHref.driver(), link, timeout, fileFilter);
      }
      case PROXY: {
        return downloadViaProxy(linkWithHref, link, timeout, fileFilter);
      }
      default: {
        throw new IllegalArgumentException("Unknown file download mode: " + config.fileDownload());
      }
    }
  }

  @CheckReturnValue
  @Nonnull
  private File downloadViaProxy(WebElementSource linkWithHref, WebElement link,
                                long timeout, FileFilter fileFilter) throws FileNotFoundException {
    Config config = linkWithHref.driver().config();
    log.debug("selenide.fileDownload = {} download file via proxy", config.fileDownload());

    if (!config.proxyEnabled()) {
      throw new IllegalStateException("Cannot download file: proxy server is not enabled. Setup proxyEnabled");
    }
    if (linkWithHref.driver().getProxy() == null) {
      throw new IllegalStateException("Cannot download file: proxy server is not started");
    }

    return downloadFileWithProxyServer.download(linkWithHref, link, timeout, fileFilter);
  }

  @CheckReturnValue
  long getTimeout(Config config, @Nullable Object[] args) {
    if (args != null && args.length > 0 && args[0] instanceof Long) {
      return (long) args[0];
    }
    else {
      return config.timeout();
    }
  }

  @CheckReturnValue
  @Nonnull
  FileFilter getFileFilter(@Nullable Object[] args) {
    if (args != null && args.length > 0 && args[0] instanceof FileFilter) {
      return (FileFilter) args[0];
    }
    if (args != null && args.length > 1 && args[1] instanceof FileFilter) {
      return (FileFilter) args[1];
    }
    else {
      return FileFilters.none();
    }
  }
}
