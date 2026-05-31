package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.DownloadFileWithCdp;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import java.io.File;

public class DownloadSingleFile extends DownloadFile implements Command<File> {
  DownloadSingleFile() {
  }

  DownloadSingleFile(DownloadFileWithHttpRequest httpGet, DownloadFileWithProxyServer proxy,
                     DownloadFileToFolder folder, DownloadFileWithCdp cdp) {
    super(httpGet, proxy, folder, cdp);
  }

  @Override
  public File execute(SelenideElement selenideElement, WebElementSource linkWithHref, Object @Nullable [] args) {
    return downloadFiles(selenideElement, linkWithHref, args).get(0);
  }
}
