package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadOptions;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.files.FileFilters;
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

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Objects.requireNonNullElse;
import static java.util.Optional.ofNullable;

public class DownloadFile implements Command<File> {
  private static final Logger log = LoggerFactory.getLogger(DownloadFile.class);

  private final DownloadFileWithHttpRequest downloadFileWithHttpRequest;
  private final DownloadFileWithProxyServer downloadFileWithProxyServer;
  private final DownloadFileToFolder downloadFileToFolder;
  private final DownloadFileWithCdp downloadFileWithCdp;

  public DownloadFile() {
    this(new DownloadFileWithHttpRequest(), new DownloadFileWithProxyServer(),
      inject(DownloadFileToFolder.class), inject(DownloadFileWithCdp.class));
  }

  DownloadFile(DownloadFileWithHttpRequest httpGet, DownloadFileWithProxyServer proxy,
               DownloadFileToFolder folder, DownloadFileWithCdp cdp) {
    downloadFileWithHttpRequest = httpGet;
    downloadFileWithProxyServer = proxy;
    downloadFileToFolder = folder;
    downloadFileWithCdp = cdp;
  }

  @Override
  public File execute(SelenideElement selenideElement, WebElementSource linkWithHref, Object @Nullable [] args) {
    WebElement link = linkWithHref.findAndAssertElementIsInteractable();
    Config config = linkWithHref.driver().config();
    DownloadOptions options = getDownloadOptions(config, args);
    long timeout = ofNullable(options.timeout()).map(Duration::toMillis).orElse(config.timeout());
    long incrementTimeout = ofNullable(options.incrementTimeout()).map(Duration::toMillis).orElse(timeout);

    log.debug("Download file: {}", options);

    FileDownloadMode method = requireNonNullElse(options.getMethod(), config.fileDownload());
    return switch (method) {
      case HTTPGET -> downloadFileWithHttpRequest.download(linkWithHref.driver(), link, timeout, options.getFilter());
      case PROXY -> downloadFileWithProxyServer.download(linkWithHref, link, timeout, options.getFilter(), options.getAction());
      case FOLDER ->
        downloadFileToFolder.download(linkWithHref, link, timeout, incrementTimeout, options.getFilter(), options.getAction());
      case CDP -> downloadFileWithCdp
        .download(linkWithHref, link, timeout, incrementTimeout, options.getFilter(), options.getAction());
    };
  }

  private DownloadOptions getDownloadOptions(Config config, Object @Nullable [] args) {
    if (args != null && args.length > 0 && args[0] instanceof DownloadOptions downloadOptions) {
      return downloadOptions;
    }
    return using(config.fileDownload())
      .withFilter(getFileFilter(args))
      .withTimeout(getTimeout(config, args));
  }

  long getTimeout(Config config, Object @Nullable [] args) {
    if (args != null && args.length > 0 && args[0] instanceof Long timeoutArgument) {
      return timeoutArgument;
    } else {
      return config.timeout();
    }
  }

  FileFilter getFileFilter(Object @Nullable [] args) {
    if (args != null && args.length > 0 && args[0] instanceof FileFilter fileFilter) {
      return fileFilter;
    }
    if (args != null && args.length > 1 && args[1] instanceof FileFilter fileFilter) {
      return fileFilter;
    } else {
      return FileFilters.none();
    }
  }
}
