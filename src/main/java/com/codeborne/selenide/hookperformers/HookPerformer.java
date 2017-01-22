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
   * @return
   */
  public static HookPerformer getInstance() {
    if (instance.get() == null) {
      instance.set(new HookPerformer());
      instance.get().beforeActions.put("flashAction", new FlashAction());
      instance.get().afterActions.put("markAction", new MarkAction());
    }
    return instance.get();
  }

  /**
   * @param element
   * @param methodName
   */
  public void beforePreform(WebElement element, String methodName) {
    preform(element, methodName, beforeActions.values());
  }

  /**
   * @param element
   * @param methodName
   */
  public void afterPreform(WebElement element, String methodName) {
    preform(element, methodName, afterActions.values());
  }

  /**
   * @param name
   * @param action
   */
  public void addBeforeAction(String name, HookAction action) {
    instance.get().beforeActions.put(name, action);
  }

  /**
   * @param name
   * @param action
   */
  public void addAfterAction(String name, HookAction action) {
    instance.get().afterActions.put(name, action);
  }

  /**
   * @param name
   */
  public void removeAction(String name) {
    if (instance.get().beforeActions.containsKey(name))
      instance.get().beforeActions.remove(name);
    if (instance.get().afterActions.containsKey(name))
      instance.get().afterActions.remove(name);
  }

  private void preform(WebElement element, String methodName, Collection<HookAction> actions) {
    for (HookAction action : actions) {
      if (action.conditionForAction(element, methodName)) action.action(element, methodName);
    }
  }
}
