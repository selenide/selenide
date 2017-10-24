package com.codeborne.selenide;

import com.codeborne.selenide.hookactions.HookAction;
import com.codeborne.selenide.hookactions.HookActions;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HookActionsTest {
  String catchAction = "";

  @After
  public void cleanUp() {
    HookActions.getInstance().removeAction("testHook");
    catchAction = "";
  }

  @Test
  public void preformBeforeHook() {
    HookActions.getInstance().addBeforeAction("testHook", new TestAction("Before action", true));
    HookActions.getInstance().beforePreform(null, "command");
    assertThat("Before action error", catchAction.equals("Before action"), is(true));
  }

  @Test
  public void preformAfterHook() {
    HookActions.getInstance().addAfterAction("testHook", new TestAction("After action", true));
    HookActions.getInstance().afterPreform(null, "command");
    assertThat("After action error", catchAction.equals("After action"), is(true));
  }

  @Test
  public void preformErrorHook() {
    HookActions.getInstance().addErrorAction("testHook", new TestAction("Error action", true));
    HookActions.getInstance().errorPreform(null, "command");
    assertThat("Error action error", catchAction.equals("Error action"), is(true));
  }

  @Test
  public void preformNotActivateHook() {
    HookActions.getInstance().addBeforeAction("testHook", new TestAction("Unactivated action", false));
    HookActions.getInstance().beforePreform(null, "command");
    assertThat("Call Unactivated Action", catchAction.equals(""), is(true));
  }

  @Test
  public void removeBeforeHook() {
    HookActions.getInstance().addBeforeAction("testHook", new TestAction("Removed Before action", true));
    HookActions.getInstance().removeAction("testHook");
    HookActions.getInstance().beforePreform(null, "command");
    assertThat("Removed Before action called", catchAction.equals(""), is(true));
  }

  @Test
  public void removeAfterHook() {
    HookActions.getInstance().addAfterAction("testHook", new TestAction("Removed After action", true));
    HookActions.getInstance().removeAction("testHook");
    HookActions.getInstance().afterPreform(null, "command");
    assertThat("Removed After action called", catchAction.equals(""), is(true));
  }

  @Test
  public void removeErrorHook() {
    HookActions.getInstance().addErrorAction("testHook", new TestAction("Removed error action", true));
    HookActions.getInstance().removeAction("testHook");
    HookActions.getInstance().errorPreform(null, "command");
    assertThat("Removed Error action called", catchAction.equals(""), is(true));
  }

  @Test
  public void preformTwoHooks() {
    HookActions.getInstance().addBeforeAction("testHook", new TestAction("hook1", true));
    HookActions.getInstance().addBeforeAction("testHook1", new TestAction("hook2", true));
    HookActions.getInstance().beforePreform(null, "command");
    assertThat("Two actions error", catchAction.equals("hook1hook2"), is(true));
    HookActions.getInstance().removeAction("testHook");
    HookActions.getInstance().removeAction("testHook1");
  }

  @Test
  public void preformHookWithCommandArgs() {
    HookActions.getInstance().addBeforeAction("testHook", new TestActionWithCommandArgs("testHook", true));
    HookActions.getInstance().beforePreform(null, "command", "one", "two", "three");
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
