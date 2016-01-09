package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.FileDownloader;
import com.codeborne.selenide.impl.WebElementSource;

import java.util.Arrays;

public class PrepareDownload implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String[] params = Arrays.copyOf(args, args.length, String[].class);
    FileDownloader.instance
                  .prepareDownloadViaBrowserMob(params[0],
                                                params[1],
                                                Arrays.asList(params).subList(2, params.length - 1));

    return proxy;
  }
}
