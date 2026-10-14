package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assumptions.assumeThat;

final class TabsWaitTest extends ITest {
  @BeforeEach
  void setUp() {
    openFile("page_with_tabs_with_delays.html");
    setTimeout(2000);
  }

  @Test
  void waitsUntilTabAppears_byTitle() {
    $("#open-new-tab-with-delay").click();
    switchTo().window("Test::alerts");
    $("h1").shouldHave(text("Page with alerts"));
  }

  @Test
  void waitsUntilTabAppears_byIndex() {
    assumeThat(browser().isHtmlUnit()).isFalse();
    $("#open-new-tab-with-delay").click();
    switchTo().window(1);
    $("h1").shouldHave(text("Page with alerts"));
  }
}
