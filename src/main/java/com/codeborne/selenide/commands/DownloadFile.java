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


  @Override
  public File execute(SelenideElement proxy, WebElementSource linkWithHref, Object[] args) throws IOException {
    WebElement link = linkWithHref.findAndAssertElementIsVisible();

    long timeout;
    try {
      if (Objects.nonNull(args) && args.length > 0) {
        timeout = (long) args[0];
      }
      else {
        timeout = Configuration.timeout;
      }
    }
    catch (ClassCastException e) {
      throw new IllegalArgumentException("Unknown target type: " + args[0] + " (only long is supported)");
    }

    if (Configuration.fileDownload == HTTPGET) {
      LOG.config("selenide.fileDownload = " + System.getProperty("selenide.fileDownload") + " download file via http get");
      return new DownloadFileWithHttpRequest(timeout).download(link);
    }
    else if (webdriverContainer.getProxyServer() == null) {
      LOG.config("Proxy server is not started - download file via http get");
      return new DownloadFileWithHttpRequest(timeout).download(link);
    }
    else {
      return new DownloadFileWithProxyServer(timeout).download(linkWithHref, link, webdriverContainer.getProxyServer());
    }
  }
}
