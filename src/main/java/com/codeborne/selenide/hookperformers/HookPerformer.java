package com.codeborne.selenide.hookperformers;

import org.openqa.selenium.WebElement;

import java.util.Collection;
import java.util.HashMap;

public class HookPerformer {
  private static ThreadLocal<HookPerformer> instance = new ThreadLocal<>();
  private HashMap<String, HookAction> beforeActions = new HashMap<>();
  private HashMap<String, HookAction> afterActions = new HashMap<>();

  private HookPerformer() {
  }

  /**
   *  Return instance of HookPerformer for current thread.
   *
   * @return HookPerformer
   */
  public static HookPerformer getInstance() {
    if (instance() == null) {
      instance.set(new HookPerformer());
      instance().addBeforeAction("delayAction", new DelayAction());
      instance().addBeforeAction("flashAction", new FlashAction());
      instance().addAfterAction("markAction", new MarkAction());
    }
    return instance();
  }

  /**
   * Run before actions
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   */
  public void beforePreform(WebElement element, String methodName) {
    preform(element, methodName, beforeActions.values());
  }

  /**
   * Run after actions
   *
   * @param element     WebElement
   * @param methodName  Name of method, who called hook
   */
  public void afterPreform(WebElement element, String methodName) {
    preform(element, methodName, afterActions.values());
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
   * Remove action from HookPerformer
   * Try to remove from before and after actions lists.
   *
   * @param name    Name of hook to remove
   */
  public void removeAction(String name) {
    if (instance.get().beforeActions.containsKey(name))
      instance.get().beforeActions.remove(name);
    if (instance.get().afterActions.containsKey(name))
      instance.get().afterActions.remove(name);
  }

  private static HookPerformer instance() {
    return instance.get();
  }

  private void preform(WebElement element, String methodName, Collection<HookAction> actions) {
    for (HookAction action : actions) {
      if (action.conditionForAction(element, methodName)) action.action(element, methodName);
    }
  }
}
