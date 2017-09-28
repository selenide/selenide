package com.codeborne.selenide.hookperformers;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HookPerformerTest {
  String catchAction = "";

  @After
  public void cleanUp() {
    HookPerformer.getInstance().removeAction("testHook");
    catchAction = "";
  }

  @Test
  public void preformBeforeHook() {
    HookPerformer.getInstance().addBeforeAction("testHook", new TestAction("Before action", true));
    HookPerformer.getInstance().beforePreform(null, "command");
    assertThat("Before action error", catchAction.equals("Before action"), is(true));
  }

  @Test
  public void preformAfterHook() {
    HookPerformer.getInstance().addAfterAction("testHook", new TestAction("After action", true));
    HookPerformer.getInstance().afterPreform(null, "command");
    assertThat("After action error", catchAction.equals("After action"), is(true));
  }

  @Test
  public void preformNotActivateHook() {
    HookPerformer.getInstance().addBeforeAction("testHook", new TestAction("Unactivated action", false));
    HookPerformer.getInstance().beforePreform(null, "command");
    assertThat("Call Unactivated Action", catchAction.equals(""), is(true));
  }

  @Test
  public void removeBeforeHook() {
    HookPerformer.getInstance().addBeforeAction("testHook", new TestAction("Removed Before action", true));
    HookPerformer.getInstance().removeAction("testHook");
    HookPerformer.getInstance().beforePreform(null, "command");
    assertThat("Removed Before action called", catchAction.equals(""), is(true));
  }

  @Test
  public void removeAfterHook() {
    HookPerformer.getInstance().addAfterAction("testHook", new TestAction("Removed After action", true));
    HookPerformer.getInstance().removeAction("testHook");
    HookPerformer.getInstance().afterPreform(null, "command");
    assertThat("Removed After action called", catchAction.equals(""), is(true));
  }

  @Test
  public void preformTwoHooks() {
    HookPerformer.getInstance().addBeforeAction("testHook", new TestAction("hook1", true));
    HookPerformer.getInstance().addBeforeAction("testHook1", new TestAction("hook2", true));
    HookPerformer.getInstance().beforePreform(null, "command");
    assertThat("Two actions error", catchAction.equals("hook1hook2"), is(true));
    HookPerformer.getInstance().removeAction("testHook");
    HookPerformer.getInstance().removeAction("testHook1");
  }

  @Test
  public void preformHookWithCommandArgs() {
    HookPerformer.getInstance().addBeforeAction("testHook", new TestActionWithCommandArgs("testHook", true));
    HookPerformer.getInstance().beforePreform(null, "command", "one", "two", "three");
    assertThat("Hook with command args usage", catchAction.equals("onetwothree"), is(true));
  }

  class TestAction implements HookAction {
    private String testString;
    private boolean isActive;

    public TestAction(String testString, boolean isActive) {
      this.testString = testString;
      this.isActive = isActive;
    }

    @Override
    public boolean conditionForAction(WebElement element, String methodName, Object... args) {
      return isActive;
    }

    @Override
    public void action(WebElement element, String methodName, Object... args) {
      catchAction += testString;
    }
  }

  class TestActionWithCommandArgs extends TestAction {

    public TestActionWithCommandArgs(String testString, boolean isActive) {
      super(testString, isActive);
    }

    @Override
    public void action(WebElement element, String methodName, Object... args) {
      for (Object arg: args) {
        catchAction += (String) arg;
      }
    }
  }
}
