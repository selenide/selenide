package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import static com.codeborne.selenide.FileDownloadMode.HTTPGET;

public class DownloadFile implements Command<File> {
  private static final Logger LOG = Logger.getLogger(DownloadFile.class.getName());

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
  public File execute(SelenideElement proxy, WebElementSource linkWithHref, Object[] args) throws IOException {
    WebElement link = linkWithHref.findAndAssertElementIsInteractable();
    Config config = linkWithHref.driver().config();

    long timeout = getTimeout(config, args);

    if (config.fileDownload() == HTTPGET) {
      LOG.config("selenide.fileDownload = " + System.getProperty("selenide.fileDownload") + " download file via http get");
      return downloadFileWithHttpRequest.download(linkWithHref.driver(), link, timeout);
    }
    if (!config.proxyEnabled()) {
      throw new IllegalStateException("Cannot download file: proxy server is not enabled. Setup proxyEnabled");
    }
    if (linkWithHref.driver().getProxy() == null) {
      throw new IllegalStateException("Cannot download file: proxy server is not started");
    }

    return downloadFileWithProxyServer.download(linkWithHref, link, linkWithHref.driver().getProxy(), timeout);
  }

  long getTimeout(Config config, Object[] args) {
    try {
      if (args != null && args.length > 0) {
        return (long) args[0];
      }
      else {
        return config.timeout();
      }
    }
    catch (ClassCastException e) {
      throw new IllegalArgumentException("Unknown target type: " + Arrays.toString(args) + " (only long is supported)");
    }
  }
}
