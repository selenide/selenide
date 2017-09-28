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
  public boolean conditionForAction(WebElement element, String methodName, Object... args) {
    return (presentationMode.active  && (presentationMode.delayBeforeCommand > 0)) && presentationMethods.contains(methodName);
  }

  @Override
  public void action(WebElement element, String methodName, Object... args) {
    sleep(presentationMode.delayBeforeCommand);
  }
}
