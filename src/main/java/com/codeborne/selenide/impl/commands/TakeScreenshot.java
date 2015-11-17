package com.codeborne.selenide.impl.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.io.File;

public class TakeScreenshot implements Command<File> {
  @Override
  public File execute(SelenideElement proxy, WebElementSource element, Object[] args) {
    return Screenshots.takeScreenShot(element.getWebElement());
  }
}
