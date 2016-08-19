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
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.webdriverContainer;
import static java.lang.System.currentTimeMillis;

public class DownloadFile implements Command<File> {
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
    FileDownloadFilter filter = proxyServer.filter("download");

    filter.activate();
    try {
      link.click();

      waiter.wait(filter, new HasDownloads());
      return firstDownloadedFile(linkWithHref, filter);
    }
    finally {
      filter.deactivate();
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
