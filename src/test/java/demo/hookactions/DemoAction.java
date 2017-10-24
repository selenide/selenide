package demo.hookactions;

import com.codeborne.selenide.hookactions.HookAction;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.Selenide.$;
import static java.util.Arrays.asList;

public class DemoAction implements HookAction {
  private String suffix;

  protected static final Set<String> hoockedActions = new HashSet<>(asList(
          "click",
          "doubleClick"
  ));

  public DemoAction(String suffix) {
    this.suffix = suffix;
  }

  @Override
  public boolean conditionForAction(WebElement element, String methodName, Object... args) {
    return hoockedActions.contains(methodName);
  }

  @Override
  public void action(WebElement element, String methodName, Object... args) {
    String locator = $(element).getSearchCriteria();
    System.out.println(String.format("%s %s%s", locator, methodName, suffix));
  }
}
