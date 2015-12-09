package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.util.List;

import static com.codeborne.selenide.commands.Util.argsToConditions;

public class ShouldHave extends Should {
  public ShouldHave() {
    super("have ");
  }
}
