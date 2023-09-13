package com.codeborne.selenide.appium.commands;

import com.codeborne.selenide.commands.Commands;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SelenideAppiumCommands extends Commands {
  public SelenideAppiumCommands() {
    add("dragAndDropTo", new AppiumDragAndDropTo());
    add("dragAndDrop", new AppiumDragAndDropTo());
    add("click", new AppiumClick());
    add("doubleClick", new AppiumDoubleClick());
    add("clear", new AppiumClear());
    add("setValue", new AppiumSetValue());
    add("val", new AppiumVal());
    add("type", new AppiumType());
    add("hideKeyboard", new HideKeyboard());
    add("scrollTo", new AppiumScrollTo());
    add("scroll", new AppiumScrollTo());
    add("swipe", new AppiumSwipeTo());
    add("swipeTo", new AppiumSwipeTo());
    add("tap", new AppiumTap());
    add("doubleTap", new AppiumDoubleTap());
  }
}
