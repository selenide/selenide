package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class TabsWaitTest extends ITest {
  @BeforeEach
  void setUp() {
    openFile("page_with_tabs_with_delays.html");
  }

  @Test
  void waitsUntilTabAppears_byTitle() {
    $("#open-new-tab-with-delay").click();
    switchTo().window("Test::alerts");
    $("h1").shouldHave(text("Page with alerts"));
  }

  @Test
  void waitsUntilTabAppears_byIndex() {
    assumeFalse(browser().isHtmlUnit(), "Sometimes it fails");

    $("#open-new-tab-with-delay").click();
    switchTo().window(1);
    $("h1").shouldHave(text("Page with alerts"));
  }
}
