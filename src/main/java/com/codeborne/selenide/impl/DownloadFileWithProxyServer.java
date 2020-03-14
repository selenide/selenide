package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.proxy.DownloadedFile;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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
                       WebElement clickable, SelenideProxyServer proxyServer, long timeout,
                       FileFilter fileFilter) throws FileNotFoundException {
    return clickAndInterceptFileByProxyServer(anyClickableElement, clickable, proxyServer, timeout, fileFilter);
  }

  private File clickAndInterceptFileByProxyServer(WebElementSource anyClickableElement, WebElement clickable,
                                                  SelenideProxyServer proxyServer, long timeout,
                                                  FileFilter fileFilter) throws FileNotFoundException {
    Config config = anyClickableElement.driver().config();
    WebDriver webDriver = anyClickableElement.driver().getWebDriver();
    String currentWindowHandle = webDriver.getWindowHandle();
    Set<String> currentWindows = webDriver.getWindowHandles();

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    filter.activate();
    try {
      waiter.wait(filter, new PreviousDownloadsCompleted(), timeout, config.pollingInterval());

      filter.reset();
      clickable.click();

      waiter.wait(filter, new HasDownloads(fileFilter), timeout, config.pollingInterval());
      return firstDownloadedFile(anyClickableElement, filter, timeout, fileFilter);
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

      log.info("File has been opened in a new window, let's close {} new windows", newWindows.size());

      for (String newWindow : newWindows) {
        log.info("  Let's close {}", newWindow);
        try {
          webDriver.switchTo().window(newWindow);
          webDriver.close();
        }
        catch (NoSuchWindowException windowHasBeenClosedMeanwhile) {
          log.info("  Failed to close {}: {}", newWindow, Cleanup.of.webdriverExceptionMessage(windowHasBeenClosedMeanwhile));
        }
        catch (Exception e) {
          log.warn("  Failed to close {}", newWindow, e);
        }
      }
      webDriver.switchTo().window(currentWindowHandle);
    }
  }

  private static class HasDownloads implements Predicate<FileDownloadFilter> {
    private final FileFilter fileFilter;

    private HasDownloads(FileFilter fileFilter) {
      this.fileFilter = fileFilter;
    }

    @Override
    public boolean test(FileDownloadFilter filter) {
      return !filter.getDownloadedFiles(fileFilter).isEmpty();
    }
  }

  private static class PreviousDownloadsCompleted implements Predicate<FileDownloadFilter> {
    private int downloadsCount = -1;

    @Override
    public boolean test(FileDownloadFilter filter) {
      try {
        return downloadsCount == filter.getDownloadedFiles().size();
      }
      finally {
        downloadsCount = filter.getDownloadedFiles().size();
      }
    }
  }

  private File firstDownloadedFile(WebElementSource anyClickableElement,
                                   FileDownloadFilter filter, long timeout, FileFilter fileFilter) throws FileNotFoundException {
    List<DownloadedFile> files = filter.getDownloadedFiles();
    if (files.isEmpty()) {
      throw new FileNotFoundException("Failed to download file " + anyClickableElement +
        " in " + timeout + " ms." + filter.responsesAsString());
    }

    log.info(filter.downloadedFilesAsString());
    log.info("Just in case, all intercepted responses: {}", filter.responsesAsString());

    return files.stream().filter(fileFilter::match).sorted(new DownloadDetector()).findFirst()
      .orElseThrow(() -> new FileNotFoundException(String.format("Failed to download file %s in %d ms.%s %n%s",
        anyClickableElement, timeout, fileFilter.description(), filter.responsesAsString())
        )
      ).getFile();
  }
}
