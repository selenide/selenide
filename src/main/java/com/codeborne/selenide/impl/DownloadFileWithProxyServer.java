package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.google.common.base.Predicate;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class DownloadFileWithProxyServer {
  private static final Logger log = Logger.getLogger(DownloadFileWithProxyServer.class.getName());

  private final Waiter waiter;

  DownloadFileWithProxyServer(Waiter waiter) {
    this.waiter = waiter;
  }

  public DownloadFileWithProxyServer() {
    this(new Waiter());
  }

  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, SelenideProxyServer proxyServer, long timeout) throws FileNotFoundException {
    return clickAndInterceptFileByProxyServer(anyClickableElement, clickable, proxyServer, timeout);
  }

  private File clickAndInterceptFileByProxyServer(WebElementSource anyClickableElement, WebElement clickable,
                                          SelenideProxyServer proxyServer, long timeout) throws FileNotFoundException {
    Config config = anyClickableElement.driver().config();
    WebDriver webDriver = anyClickableElement.driver().getWebDriver();
    String currentWindowHandle = webDriver.getWindowHandle();
    Set<String> currentWindows = webDriver.getWindowHandles();

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    filter.activate();
    try {
      clickable.click();

      waiter.wait(filter, new HasDownloads(), timeout, config.pollingInterval());
      return firstDownloadedFile(anyClickableElement, filter, timeout);
    }
    finally {
      filter.deactivate();
      closeNewWindows(webDriver, currentWindowHandle, currentWindows);
    }
  }

  private void closeNewWindows(WebDriver webDriver, String currentWindowHandle, Set<String> currentWindows) {
    Set<String> windowHandles = webDriver.getWindowHandles();
    if (windowHandles.size() != currentWindows.size()) {
      Set<String> newWindows = new HashSet<>(windowHandles);
      newWindows.removeAll(currentWindows);

      log.info("File has been opened in a new window, let's close " + newWindows.size() + " new windows");

      for (String newWindow : newWindows) {
        log.info("  Let's close " + newWindow);
        try {
          webDriver.switchTo().window(newWindow);
          webDriver.close();
        }
        catch (NoSuchWindowException windowHasBeenClosedMeanwhile) {
          log.info("  Failed to close " + newWindow + ": " + Cleanup.of.webdriverExceptionMessage(windowHasBeenClosedMeanwhile));
        }
        catch (Exception e) {
          log.warning("  Failed to close " + newWindow + ": " + e);
        }
      }
      webDriver.switchTo().window(currentWindowHandle);
    }
  }

  private static class HasDownloads implements Predicate<FileDownloadFilter> {
    @Override
    public boolean apply(FileDownloadFilter filter) {
      return !filter.getDownloadedFiles().isEmpty();
    }
  }

  private File firstDownloadedFile(WebElementSource anyClickableElement,
                                   FileDownloadFilter filter, long timeout) throws FileNotFoundException {
    List<File> files = filter.getDownloadedFiles();
    if (files.isEmpty()) {
      throw new FileNotFoundException("Failed to download file " + anyClickableElement +
        " in " + timeout + " ms." + filter.getResponses());
    }

    log.info("Downloaded file: " + files.get(0).getAbsolutePath());
    log.info("Just in case, all intercepted responses: " + filter.getResponses());
    return files.get(0);
  }
}
