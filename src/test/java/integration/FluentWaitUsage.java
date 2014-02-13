package integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.Wait;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.Thread.currentThread;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class FluentWaitUsage {
  @Before
  public void openTestPageWithJQuery() {
    open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @Test
  public void canUseSeleniumFluentWaitAPI() {
    Wait().until(invisibilityOfElementLocated(By.id("magic-id")));
    Wait().until(presenceOfAllElementsLocatedBy(By.tagName("h1")));
    Wait().until(textToBePresentInElementLocated(By.tagName("h2"), "Dropdown list"));
  }
}
