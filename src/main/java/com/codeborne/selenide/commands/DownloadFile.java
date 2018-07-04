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
import java.util.Objects;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;

public class DownloadFile implements Command<File> {
  private static final Logger LOG = Logger.getLogger(DownloadFile.class.getName());

  DownloadFileWithHttpRequest downloadFileWithHttpRequest = new DownloadFileWithHttpRequest();
  DownloadFileWithProxyServer downloadFileWithProxyServer = new DownloadFileWithProxyServer();

  @Override
  public File execute(SelenideElement proxy, WebElementSource linkWithHref, Object[] args) throws IOException {
    WebElement link = linkWithHref.findAndAssertElementIsVisible();

    long timeout = getTimeout(args);

    if (Configuration.fileDownload == HTTPGET) {
      LOG.config("selenide.fileDownload = " + System.getProperty("selenide.fileDownload") + " download file via http get");
      return downloadFileWithHttpRequest.download(link, timeout);
    }
    else if (webdriverContainer.getProxyServer() == null) {
      LOG.config("Proxy server is not started - download file via http get");
      return downloadFileWithHttpRequest.download(link, timeout);
    }
    else {
      return downloadFileWithProxyServer.download(linkWithHref, link, webdriverContainer.getProxyServer(), timeout);
    }
  }

  private long getTimeout(Object[] args) {
    try {
      if (Objects.nonNull(args) && args.length > 0) {
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
