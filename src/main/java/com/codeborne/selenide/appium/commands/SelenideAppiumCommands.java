package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.commands.Commands;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SelenideAppiumCommands extends Commands {
  public SelenideAppiumCommands() {
    add("dragAndDropTo", new AppiumDragAndDropTo());
    add("click", new AppiumClick());
    add("clear", new AppiumClear());
    add("setValue", new AppiumSetValue());
    add("val", new AppiumVal());
    add("hideKeyboard", new HideKeyboard());
  }
}
