package com.codeborne.selenide.hookperformers;

import org.openqa.selenium.WebElement;

public interface HookAction {
  /**
   * Condition for activation
   *
   * @param element WebElement
   * @param methodName  Name of method, who called hook
   * @return boolean
   *
   */
  boolean conditionForAction(WebElement element, String methodName);

  /**
   * Action when activated
   *
   * @param element WebElement
   * @param methodName  name of method, who called hook
   */
  void action(WebElement element, String methodName);
}
