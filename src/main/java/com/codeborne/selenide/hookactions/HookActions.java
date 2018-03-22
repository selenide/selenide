package com.codeborne.selenide.hookactions;

import org.openqa.selenium.WebElement;

import java.util.Collection;
import java.util.HashMap;

public class HookActions {
  private static ThreadLocal<HookActions> instance = new ThreadLocal<>();
  private HashMap<String, HookAction> beforeActions = new HashMap<>();
  private HashMap<String, HookAction> afterActions = new HashMap<>();
  private HashMap<String, HookAction> errorActions = new HashMap<>();

  private HookActions() {
  }

  /**
   *  Return instance of HookActions for current thread.
   *
   * @return HookActions
   */
  public static HookActions getInstance() {
    if (instance() == null) {
      instance.set(new HookActions());
    }
    return instance();
  }

  /**
   * Run actions before command
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   * @param args        Called command arguments
   */
  public void beforePerform(WebElement element, String methodName, Object... args) {
    perform(element, methodName, beforeActions.values(), args);
  }

  /**
   * Run actions after command
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   * @param args        Called command arguments
   */
  public void afterPerform(WebElement element, String methodName, Object... args) {
    perform(element, methodName, afterActions.values(), args);
  }

  /**
   * Run action when command ends with error
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   * @param args        Called command arguments
   */
  public void errorPerform(WebElement element, String methodName, Object... args) {
    perform(element, methodName, errorActions.values(), args);
  }

  /**
   * Register new before action
   *
   * @param name    Name of registered hook
   * @param action  HookAction implementation
   */
  public void addBeforeAction(String name, HookAction action) {
    instance.get().beforeActions.put(name, action);
  }

  /**
   * Register new after action
   *
   * @param name    Name of registered hook
   * @param action  HookAction implementation
   */
  public void addAfterAction(String name, HookAction action) {
    instance.get().afterActions.put(name, action);
  }

  /**
   * Register new error action
   *
   * @param name    Name of registered hook
   * @param action  HookAction implementation
   */
  public void addErrorAction(String name, HookAction action) {
    instance.get().errorActions.put(name, action);
  }

  /**
   * Remove action from before HookActions list
   *
   * @param name    Name of hook to remove
   */
  public void removeBeforeAction(String name) {
    instance.get().beforeActions.remove(name);
  }

  /**
   * Remove action from after HookActions list
   *
   * @param name    Name of hook to remove
   */
  public void removeAfterAction(String name) {
    instance.get().afterActions.remove(name);
  }

  /**
   * Remove action from error HookActions list
   *
   * @param name    Name of hook to remove
   */
  public void removeErrorAction(String name) {
    instance.get().errorActions.remove(name);
  }

  private static HookActions instance() {
    return instance.get();
  }

  private void perform(WebElement element, String methodName, Collection<HookAction> actions, Object... args) {
    for (HookAction action : actions) {
      if (action.isActive(element, methodName, args)) action.action(element, methodName, args);
    }
  }
}
