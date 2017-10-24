package integration;

import com.codeborne.selenide.hookactions.HookAction;
import com.codeborne.selenide.hookactions.HookActions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HookActionsTest  extends IntegrationTest {
  String catchAction = "";

  @Before
  public void setUp() {
    openFile("page_with_big_divs.html");
  }

  @After
  public void cleanUp() {
    HookActions.getInstance().removeAction("testHook");
    catchAction = "";
  }

  @Test
  public void beforeActionTest() {
    HookActions.getInstance().addBeforeAction("testHook", new TestAction(true));
    $("div", 0).click();
    assertThat("Before action error", catchAction.equals("click"), is(true));
  }

  @Test
  public void afterActionTest() {
    HookActions.getInstance().addAfterAction("testHook", new TestAction(true));
    $("div", 0).click();
    assertThat("After action error", catchAction.equals("click"), is(true));
  }

  @Test
  public void errorActionTest() {
    HookActions.getInstance().addErrorAction("testHook", new TestAction(true));
    try {
      $("div.these.are.not.droid.what.you.looking.for").click();
    } catch (Throwable e) { }
    assertThat("Error action error", catchAction.equals("click"), is(true));
  }

  @Test
  public void twoActionsTest() {
    HookActions.getInstance().addBeforeAction("testHook", new TestAction(true));
    HookActions.getInstance().addAfterAction("testHook", new TestAction(true));
    $("div", 0).click();
    assertThat("After action error", catchAction.equals("clickclick"), is(true));
  }

  @Test
  public void actionsWithArgsTest() {
    HookActions.getInstance().addBeforeAction("testHook", new TestActionWithArgs());
    $("div", 0).getAttribute("id");
    assertThat("Action with args error", catchAction.equals("id"), is(true));
  }

  class TestAction implements HookAction {
    private boolean isActive;

    TestAction(boolean isActive) {
      this.isActive = isActive;
    }

    @Override
    public boolean conditionForAction(WebElement element, String methodName, Object... args) {
      return isActive;
    }

    @Override
    public void action(WebElement element, String methodName, Object... args) {
      catchAction += methodName;
    }
  }

  class TestActionWithArgs implements HookAction {

    @Override
    public boolean conditionForAction(WebElement element, String methodName, Object... args) {
      return true;
    }

    @Override
    public void action(WebElement element, String methodName, Object... args) {
      catchAction += args[0].toString();
    }
  }
}
