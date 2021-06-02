package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadOptions;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.files.FileFilters;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class DownloadFile implements Command<File> {
  private static final Logger log = LoggerFactory.getLogger(DownloadFile.class);

  private final DownloadFileWithHttpRequest downloadFileWithHttpRequest;
  private final DownloadFileWithProxyServer downloadFileWithProxyServer;
  private final DownloadFileToFolder downloadFileToFolder;

  public DownloadFile() {
    this(new DownloadFileWithHttpRequest(), new DownloadFileWithProxyServer(), inject(DownloadFileToFolder.class));
  }

  DownloadFile(DownloadFileWithHttpRequest httpget, DownloadFileWithProxyServer proxy, DownloadFileToFolder folder) {
    downloadFileWithHttpRequest = httpget;
    downloadFileWithProxyServer = proxy;
    downloadFileToFolder = folder;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public File execute(SelenideElement selenideElement, WebElementSource linkWithHref, @Nullable Object[] args) throws IOException {
    WebElement link = linkWithHref.findAndAssertElementIsInteractable();
    Config config = linkWithHref.driver().config();
    DownloadOptions options = getDownloadOptions(config, args);
    long timeout = options.getTimeout(config.timeout());

    log.debug("Download file: {}", options);

    switch (options.getMethod()) {
      case HTTPGET: {
        return downloadFileWithHttpRequest.download(linkWithHref.driver(), link, timeout, options.getFilter());
      }
      case PROXY: {
        return downloadFileWithProxyServer.download(linkWithHref, link, timeout, options.getFilter(), options.getAction());
      }
      case FOLDER: {
        return downloadFileToFolder.download(linkWithHref, link, timeout, options.getFilter(), options.getAction());
      }
      default: {
        throw new IllegalArgumentException("Unknown file download mode: " + options.getMethod());
      }
    }
  }

  @CheckReturnValue
  @Nonnull
  private DownloadOptions getDownloadOptions(Config config, @Nullable Object[] args) {
    if (args != null && args.length > 0 && args[0] instanceof DownloadOptions) {
      return (DownloadOptions) args[0];
    }
    return using(config.fileDownload())
      .withFilter(getFileFilter(args))
      .withTimeout(getTimeout(config, args));
  }

  @CheckReturnValue
  long getTimeout(Config config, @Nullable Object[] args) {
    if (args != null && args.length > 0 && args[0] instanceof Long) {
      return (long) args[0];
    }
    else {
      return config.timeout();
    }
  }

  @CheckReturnValue
  @Nonnull
  FileFilter getFileFilter(@Nullable Object[] args) {
    if (args != null && args.length > 0 && args[0] instanceof FileFilter) {
      return (FileFilter) args[0];
    }
    if (args != null && args.length > 1 && args[1] instanceof FileFilter) {
      return (FileFilter) args[1];
    }
    else {
      return FileFilters.none();
    }
  }
}
