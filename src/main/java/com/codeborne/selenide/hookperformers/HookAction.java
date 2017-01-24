package com.codeborne.selenide.hookperformers;

import org.openqa.selenium.WebElement;

public interface HookAction {
  /**
   * Condition for activation
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   * @param args        Called command arguments
   * @return boolean
   *
   */
  boolean conditionForAction(WebElement element, String methodName, Object... args);

  /**
   * Action when activated
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   * @param args        Called command arguments
   */
  void action(WebElement element, String methodName, Object... args);
}
