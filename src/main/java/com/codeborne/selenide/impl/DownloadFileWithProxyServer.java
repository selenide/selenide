package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.google.common.base.Predicate;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class DownloadFileWithProxyServer {
  private static final Logger log = Logger.getLogger(DownloadFileWithProxyServer.class.getName());

  Waiter waiter = new Waiter();

  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, SelenideProxyServer proxyServer, long timeout) throws FileNotFoundException {
    return clickAndInterceptFileByProxyServer(anyClickableElement, clickable, proxyServer, timeout);
  }

  private File clickAndInterceptFileByProxyServer(WebElementSource anyClickableElement, WebElement clickable,
                                          SelenideProxyServer proxyServer, long timeout) throws FileNotFoundException {
    String currentWindowHandle = getWebDriver().getWindowHandle();
    Set<String> currentWindows = getWebDriver().getWindowHandles();

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    filter.activate();
    try {
      clickable.click();

      waiter.wait(filter, new HasDownloads(), timeout, Configuration.pollingInterval);
      return firstDownloadedFile(anyClickableElement, filter, timeout);
    }
    finally {
      filter.deactivate();
      closeNewWindows(currentWindowHandle, currentWindows);
    }
  }

  private void closeNewWindows(String currentWindowHandle, Set<String> currentWindows) {
    Set<String> windowHandles = getWebDriver().getWindowHandles();
    if (windowHandles.size() != currentWindows.size()) {
      Set<String> newWindows = new HashSet<>(windowHandles);
      newWindows.removeAll(currentWindows);

      log.info("File has been opened in a new window, let's close " + newWindows.size() + " new windows");

      for (String newWindow : newWindows) {
        log.info("  Let's close " + newWindow);
        try {
          getWebDriver().switchTo().window(newWindow);
          getWebDriver().close();
        }
        catch (NoSuchWindowException windowHasBeenClosedMeanwhile) {
          log.info("  Failed to close " + newWindow + ": " + Cleanup.of.webdriverExceptionMessage(windowHasBeenClosedMeanwhile));
        }
        catch (Exception e) {
          log.warning("  Failed to close " + newWindow + ": " + e);
        }
      }
      getWebDriver().switchTo().window(currentWindowHandle);
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
