package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.List;

public class DownloadMultipleFiles extends DownloadFile implements Command<List<File>> {
  @Override
  public List<File> execute(SelenideElement selenideElement, WebElementSource linkWithHref, Object @Nullable [] args) {
    return downloadFiles(selenideElement, linkWithHref, args);
  }
}
