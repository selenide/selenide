package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.commands.Commands;

public class SelenideAppiumCommands extends Commands {
  public SelenideAppiumCommands() {
    add("dragAndDropTo", new AppiumDragAndDropTo());
    add("click", new AppiumClick());
  }
}
