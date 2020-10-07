package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.Wait;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;

final class FluentWaitUsage extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canUseSeleniumFluentWaitAPI() {
    Wait().until(invisibilityOfElementLocated(By.id("magic-id")));
    Wait().until(presenceOfAllElementsLocatedBy(By.tagName("h1")));
    Wait().until(textToBePresentInElementLocated(By.tagName("h2"), "Dropdown list"));
  }
}
