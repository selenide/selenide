package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.WindowNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Set;

import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class TabsTest extends ITest {
  @BeforeEach
  void setUp() {
    setTimeout(1000);
    openFile("page_with_tabs.html");
  }

  @Test
  void userCanBrowseTabs_webdriver_api() {
    WebDriver driver = driver().getWebDriver();

    $(byText("Page1: uploads")).click();
    driver().Wait().until(ExpectedConditions.numberOfWindowsToBe(2));

    $("h1").shouldHave(text("Tabs"));

    String windowHandle = driver.getWindowHandle();

    driver.switchTo().window(nextWindowHandle(driver));
    $("h1").shouldHave(text("File uploads"));

    driver.switchTo().window(windowHandle);
    $("h1").shouldHave(text("Tabs"));
  }

  private String nextWindowHandle(WebDriver driver) {
    String windowHandle = driver.getWindowHandle();
    Set<String> windowHandles = driver.getWindowHandles();
    windowHandles.remove(windowHandle);

    return windowHandles.iterator().next();
  }

  @Test
  void canSwitchToWindowByTitle() {
    $(byText("Page2: alerts")).click();
    $(byText("Page1: uploads")).click();
    $(byText("Page3: jquery")).click();

    $("h1").shouldHave(text("Tabs"));
    switchTo().window("Test::alerts");
    $("h1").shouldHave(text("Page with alerts"));
    switchTo().window("Test::jquery");
    $("h1").shouldHave(text("Page with JQuery"));
    switchTo().window("Test::uploads");
    $("h1").shouldHave(text("File uploads"));
    switchTo().window("Test::tabs");
    $("h1").shouldHave(text("Tabs"));
  }

  @Test
  void canSwitchToWindowByIndex_chrome() {
    assumeTrue(browser().isChrome());
    $(byText("Page2: alerts")).click();
    $(byText("Page1: uploads")).click();
    $(byText("Page3: jquery")).click();

    $("h1").shouldHave(text("Tabs"));

    switchTo().window(1);
    $("h1").shouldHave(text("Page with JQuery"));
    switchTo().window(2);
    $("h1").shouldHave(text("File uploads"));
    switchTo().window(3);
    $("h1").shouldHave(text("Page with alerts"));
    switchTo().window(0);
    $("h1").shouldHave(text("Tabs"));
  }

  @Test
  void canSwitchToWindowByIndex_other_browsers_but_chrome() {
    assumeFalse(browser().isChrome());
    $(byText("Page2: alerts")).click();
    $(byText("Page1: uploads")).click();
    $(byText("Page3: jquery")).click();

    $("h1").shouldHave(text("Tabs"));
    Condition oneOfTitles = or("one of titles", text("Tabs"), text("Page with alerts"), text("File uploads"), text("Page with JQuery"));

    switchTo().window(1);
    $("h1").shouldHave(oneOfTitles);
    String title1 = $("h1").text();

    switchTo().window(2);
    $("h1").shouldHave(oneOfTitles);
    String title2 = $("h1").text();

    switchTo().window(3);
    $("h1").shouldHave(oneOfTitles);
    String title3 = $("h1").text();

    switchTo().window(0);
    $("h1").shouldHave(oneOfTitles);
    String title0 = $("h1").text();

    assertThat(asList(title0, title1, title2, title3))
      .containsExactlyInAnyOrder("Tabs", "Page with alerts", "File uploads", "Page with JQuery");
  }

  @Test
  void canSwitchBetweenWindowsWithSameTitles() {
    $(byText("Page4: same title")).click();
    $("h1").shouldHave(text("Tabs"));

    switchTo().window("Test::tabs::title");
    $("body").shouldHave(or("one of tabs with this title", text("Secret phrase 1"), text("Secret phrase 2"), text("Secret phrase 3")));

    switchTo().window(0);
    $("h1").shouldHave(text("Tabs"));
    $(byText("Page5: same title")).click();
    switchTo().window("Test::tabs::title");
    $("body").shouldHave(or("one of tabs with this title", text("Secret phrase 1"), text("Secret phrase 2"), text("Secret phrase 3")));
    switchTo().window(0);
    $("h1").shouldHave(text("Tabs"));
  }

  @Test
  void throwsNoSuchWindowExceptionWhenSwitchingToAbsentWindowByTitle() {
    assertThat(driver().title())
      .isEqualTo("Test::tabs");

    assertThatThrownBy(() -> switchTo().window("absentWindow"))
      .isInstanceOf(WindowNotFoundException.class)
      .hasMessageStartingWith("No window found with name or handle or title: absentWindow")
      .hasMessageContaining("Screenshot: file:")
      .hasMessageContaining("Page source: file:")
      .hasMessageContaining("Caused by: TimeoutException:");
  }

  @Test
  void throwsNoSuchWindowExceptionWhenSwitchingToAbsentWindowByIndex() {
    assertThat(driver().title())
      .isEqualTo("Test::tabs");

    assertThatThrownBy(() -> switchTo().window(Integer.MAX_VALUE))
      .isInstanceOf(WindowNotFoundException.class)
      .hasMessageStartingWith("No window found with index: " + Integer.MAX_VALUE)
      .hasMessageContaining("Screenshot: file:")
      .hasMessageContaining("Page source: file:")
      .hasMessageContaining("Caused by: TimeoutException:");
  }

  @AfterEach
  void tearDown() {
    driver().close();
  }
}
