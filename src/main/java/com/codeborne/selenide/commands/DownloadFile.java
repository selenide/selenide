package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.FileDownloader;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static java.lang.System.currentTimeMillis;

public class DownloadFile implements Command<File> {
  private static final Logger log = Logger.getLogger(DownloadFile.class.getName());
  
  Waiter waiter = new Waiter();
  
  @Override
  public File execute(SelenideElement proxy, WebElementSource linkWithHref, Object[] args) throws IOException {
    WebElement link = linkWithHref.findAndAssertElementIsVisible();
    if ("legacy".equals(System.getProperty("selenide.fileDownload"))) {
      return FileDownloader.instance.download(link);
    }
    else {
      return clickAndInterceptFileByProxyServer(linkWithHref, link, webdriverContainer.getProxyServer());
    }
  }

  File clickAndInterceptFileByProxyServer(WebElementSource linkWithHref, WebElement link, 
                                                  SelenideProxyServer proxyServer) throws FileNotFoundException {
    String currentWindowHandle = getWebDriver().getWindowHandle();
    Set<String> currentWindows = getWebDriver().getWindowHandles();
    
    FileDownloadFilter filter = proxyServer.responseFilter("download");
    filter.activate();
    try {
      link.click();

      waiter.wait(filter, new HasDownloads());
      return firstDownloadedFile(linkWithHref, filter);
    }
    finally {
      filter.deactivate();
      closeNewWindows(currentWindowHandle, currentWindows);
    }
  }

  private void closeNewWindows(String currentWindowHandle, Set<String> currentWindows) {
    if (getWebDriver().getWindowHandles().size() != currentWindows.size()) {
      Set<String> newWindows = new HashSet<>(getWebDriver().getWindowHandles());
      newWindows.removeAll(currentWindows);

      log.info("File has been opened in a new window, let's close " + newWindows.size() + " new windows");
      for (String newWindow : newWindows) {
        log.info("  Let's close " + newWindow);
        getWebDriver().switchTo().window(newWindow);
        getWebDriver().close();
      }
      getWebDriver().switchTo().window(currentWindowHandle);
    }
  }

  static class Waiter {
    public <T> void wait(T subject, Predicate<T> condition) {
      wait(subject, condition, Configuration.timeout, Configuration.pollingInterval);
    }
    
    public <T> void wait(T subject, Predicate<T> condition, long timeout, long pollingInterval) {
      for (long start = currentTimeMillis();
           !isTimeoutExceeded(timeout, start) && !condition.apply(subject); ) {
        sleep(pollingInterval);
      }
    }

    private boolean isTimeoutExceeded(long timeout, long start) {
      return currentTimeMillis() - start > timeout;
    }

    void sleep(long milliseconds) {
      try {
        Thread.sleep(milliseconds);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    }
  }
  
  private static class HasDownloads implements Predicate<FileDownloadFilter> {
    @Override
    public boolean apply(FileDownloadFilter filter) {
      return !filter.getDownloadedFiles().isEmpty();
    }
  }

  private File firstDownloadedFile(WebElementSource linkWithHref, FileDownloadFilter filter) throws FileNotFoundException {
    List<File> files = filter.getDownloadedFiles();
    if (files.isEmpty()) {
      throw new FileNotFoundException("Failed to download file " + linkWithHref);

    }
    return files.get(0);
  }
}
