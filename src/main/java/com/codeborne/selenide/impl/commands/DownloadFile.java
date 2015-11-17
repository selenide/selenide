package com.codeborne.selenide.impl.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.impl.FileDownloader;

import java.io.File;
import java.io.IOException;

public class DownloadFile implements Command<File> {
  @Override
  public File execute(SelenideElement proxy, WebElementSource linkWithHref, Object[] args) throws IOException {
    return FileDownloader.instance.download(linkWithHref.getWebElement());
  }
}
