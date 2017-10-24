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
  public void beforePreform(WebElement element, String methodName, Object... args) {
    preform(element, methodName, beforeActions.values(), args);
  }

  /**
   * Run actions after command
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   * @param args        Called command arguments
   */
  public void afterPreform(WebElement element, String methodName, Object... args) {
    preform(element, methodName, afterActions.values(), args);
  }

  /**
   * Run action when command ends with error
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   * @param args        Called command arguments
   */
  public void errorPreform(WebElement element, String methodName, Object... args) {
    preform(element, methodName, errorActions.values(), args);
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
   * Remove action from HookActions
   * Try to remove from before, after and error actions lists.
   *
   * @param name    Name of hook to remove
   */
  public void removeAction(String name) {
    if (instance.get().beforeActions.containsKey(name))
      instance.get().beforeActions.remove(name);
    if (instance.get().afterActions.containsKey(name))
      instance.get().afterActions.remove(name);
    if (instance.get().errorActions.containsKey(name))
      instance.get().errorActions.remove(name);
  }

  private static HookActions instance() {
    return instance.get();
  }

  private void preform(WebElement element, String methodName, Collection<HookAction> actions, Object... args) {
    for (HookAction action : actions) {
      if (action.conditionForAction(element, methodName, args)) action.action(element, methodName, args);
    }
  }
}
