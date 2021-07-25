package com.codeborne.selenide.files;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

@FunctionalInterface
public interface DownloadAction {
  void perform(Driver driver, WebElement link);
}
