package com.codeborne.selenide.hookactions;

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
   * Action, if conditionForAction is true
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   * @param args        Called command arguments
   */
  void action(WebElement element, String methodName, Object... args);
}
