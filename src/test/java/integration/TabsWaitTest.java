package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public class TabsWaitTest extends IntegrationTest {
  @Before
  public void setUp() {
    openFile("page_with_tabs_with_delays.html");
  }

  @Test
  public void waitsUntilTabAppears_byTitle() {
    $("#open-new-tab-with-delay").click();
    switchTo().window("Test::alerts");
    $("h1").shouldHave(text("Page with alerts"));
  }

  @Test
  public void waitsUntilTabAppears_byIndex() {
    $("#open-new-tab-with-delay").click();
    switchTo().window(1);
    $("h1").shouldHave(text("Page with alerts"));
  }
}
