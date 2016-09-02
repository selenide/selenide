package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;

public class DownloadFile implements Command<File> {
  private static final Logger LOG = Logger.getLogger(DownloadFile.class.getName());
  
  DownloadFileWithHttpRequest downloadFileWithHttpRequest = new DownloadFileWithHttpRequest();
  DownloadFileWithProxyServer downloadFileWithProxyServer = new DownloadFileWithProxyServer();
  
  @Override
  public File execute(SelenideElement proxy, WebElementSource linkWithHref, Object[] args) throws IOException {
    WebElement link = linkWithHref.findAndAssertElementIsVisible();
    if (webdriverContainer.getProxyServer() == null) {
      LOG.config("Proxy server is not started - download file via http get");
      return downloadFileWithHttpRequest.download(link);
    }
    else {
      return downloadFileWithProxyServer.download(linkWithHref, link, webdriverContainer.getProxyServer());
    }
  }
}
