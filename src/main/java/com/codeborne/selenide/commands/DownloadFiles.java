package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadFilesOptions;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.StopCommandExecutionException;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.DownloadFileWithCdp;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.impl.DurationFormat.formatDuration;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Objects.requireNonNullElse;
import static java.util.Optional.ofNullable;

public class DownloadFiles implements Command<List<File>> {
  private static final Logger log = LoggerFactory.getLogger(DownloadFiles.class);

  private final DownloadFileWithHttpRequest downloadFileWithHttpRequest;
  private final DownloadFileWithProxyServer downloadFileWithProxyServer;
  private final DownloadFileToFolder downloadFileToFolder;
  private final DownloadFileWithCdp downloadFileWithCdp;

  public DownloadFiles() {
    this(new DownloadFileWithHttpRequest(), new DownloadFileWithProxyServer(),
      inject(DownloadFileToFolder.class), inject(DownloadFileWithCdp.class));
  }

  DownloadFiles(DownloadFileWithHttpRequest httpGet, DownloadFileWithProxyServer proxy,
                DownloadFileToFolder folder, DownloadFileWithCdp cdp) {
    downloadFileWithHttpRequest = httpGet;
    downloadFileWithProxyServer = proxy;
    downloadFileToFolder = folder;
    downloadFileWithCdp = cdp;
  }

  @Override
  public List<File> execute(SelenideElement selenideElement, WebElementSource linkWithHref, Object @Nullable [] args) {
    if (args == null || args.length == 0 || !(args[0] instanceof DownloadFilesOptions options)) {
      throw new IllegalArgumentException("downloadFiles requires a DownloadFilesOptions argument");
    }
    Config config = linkWithHref.driver().config();
    long timeout = ofNullable(options.timeout()).map(Duration::toMillis).orElse(config.timeout());
    long incrementTimeout = ofNullable(options.incrementTimeout()).map(Duration::toMillis).orElse(timeout);
    if (timeout < incrementTimeout) {
      String error = "Timeout (%s ms) must be greater than increment timeout (%s ms)".formatted(timeout, incrementTimeout);
      throw new IllegalArgumentException(error);
    }

    FileDownloadMode method = requireNonNullElse(options.getMethod(), config.fileDownload());
    DownloadFilesOptions resolved = options.getMethod() == null ? options.withMethod(method) : options;
    validateMode(resolved);

    log.debug("Download {} files with method {}, timeout {}, incTimeout {}",
      resolved.expectedFileCount(), method, formatDuration(timeout), formatDuration(incrementTimeout));

    WebElement link = waitForLink(linkWithHref, incrementTimeout);

    return switch (method) {
      case HTTPGET -> downloadFileWithHttpRequest.downloadFiles(linkWithHref.driver(), link, timeout, resolved);
      case PROXY -> downloadFileWithProxyServer.downloadFiles(linkWithHref, link, timeout, resolved);
      case FOLDER -> downloadFileToFolder.downloadFiles(linkWithHref, link, timeout, incrementTimeout, resolved);
      case CDP -> downloadFileWithCdp.downloadFiles(linkWithHref, link, timeout, incrementTimeout, resolved);
    };
  }

  void validateMode(DownloadFilesOptions options) {
    FileDownloadMode method = options.getMethod();
    if (method == FileDownloadMode.HTTPGET && options.expectedFileCount() > 1) {
      throw new UnsupportedOperationException(
        "HTTPGET mode downloads a single href; use FileDownloadMode.FOLDER, CDP, or PROXY for multi-file downloads");
    }
  }

  private static WebElement waitForLink(WebElementSource linkWithHref, long incrementTimeout) {
    try {
      return linkWithHref.findAndAssertElementIsInteractable();
    }
    catch (ElementNotFound elementNotFound) {
      throw new StopCommandExecutionException(elementNotFound, incrementTimeout);
    }
  }
}
