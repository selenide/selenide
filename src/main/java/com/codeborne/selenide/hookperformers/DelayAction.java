package com.codeborne.selenide.hookperformers;

import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.Configuration.presentationMode;
import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Arrays.asList;

public class DelayAction implements HookAction {
  protected static final Set<String> presentationMethods = new HashSet<>(asList(
          "click",
          "contextClick",
          "doubleClick",
          "followLink"
  ));

  @Override
  public boolean conditionForAction(WebElement element, String methodName) {
    return presentationMode.active && presentationMethods.contains(methodName) && (presentationMode.delayBeforeCommand > 0);
  }

  @Override
  public void action(WebElement element, String methodName) {
    sleep(presentationMode.delayBeforeCommand);
  }
}
