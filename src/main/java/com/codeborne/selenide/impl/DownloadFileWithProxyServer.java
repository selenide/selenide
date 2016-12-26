package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.google.common.base.Predicate;
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
                       WebElement clickable, SelenideProxyServer proxyServer) throws FileNotFoundException {
    return clickAndInterceptFileByProxyServer(anyClickableElement, clickable, proxyServer);
  }
  
  private File clickAndInterceptFileByProxyServer(WebElementSource anyClickableElement, WebElement clickable,
                                          SelenideProxyServer proxyServer) throws FileNotFoundException {
    String currentWindowHandle = getWebDriver().getWindowHandle();
    Set<String> currentWindows = getWebDriver().getWindowHandles();

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    filter.activate();
    try {
      clickable.click();

      waiter.wait(filter, new HasDownloads());
      return firstDownloadedFile(anyClickableElement, filter);
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
        try {
          getWebDriver().close();
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
                                   FileDownloadFilter filter) throws FileNotFoundException {
    List<File> files = filter.getDownloadedFiles();
    if (files.isEmpty()) {
      throw new FileNotFoundException("Failed to download file " + anyClickableElement +
          " in " + Configuration.timeout + " ms." + filter.getResponses());

    }
    
    log.info("Downloaded file: " + files.get(0).getAbsolutePath());
    log.info("Just in case, all intercepted responses: " + filter.getResponses());
    return files.get(0);
  }
}
