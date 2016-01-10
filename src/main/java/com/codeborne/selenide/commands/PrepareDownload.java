package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.FileDownloader;
import com.codeborne.selenide.impl.WebElementSource;

import java.util.Arrays;

public class PrepareDownload implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String[] types = (String[]) args[2];

    FileDownloader.instance
                  .prepareDownloadViaBrowserMob((String) args[0],
                                                (String) args[1],
                                                Arrays.asList(types));

    return proxy;
  }
}
