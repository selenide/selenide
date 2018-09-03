package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;

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
    WebElement link = linkWithHref.findAndAssertElementIsVisible();

    long timeout = getTimeout(args);

    if (Configuration.fileDownload == HTTPGET) {
      LOG.config("selenide.fileDownload = " + System.getProperty("selenide.fileDownload") + " download file via http get");
      return downloadFileWithHttpRequest.download(linkWithHref.context(), link, timeout);
    }
    if (!Configuration.proxyEnabled) {
      throw new IllegalStateException("Cannot download file: proxy server is not enabled. Setup Configuration.proxyEnabled");
    }
    if (linkWithHref.context().getProxy() == null) {
      throw new IllegalStateException("Cannot download file: proxy server is not started");
    }

    return downloadFileWithProxyServer.download(linkWithHref, link, linkWithHref.context().getProxy(), timeout);
  }

  long getTimeout(Object[] args) {
    try {
      if (args != null && args.length > 0) {
        return (long) args[0];
      }
      else {
        return Configuration.timeout;
      }
    }
    catch (ClassCastException e) {
      throw new IllegalArgumentException("Unknown target type: " + args[0] + " (only long is supported)");
    }
  }
}
