package integration;

import com.automation.remarks.video.annotations.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Set;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class TabsTest extends ITest {
  @BeforeEach
  void setUp() {
    openFile("page_with_tabs.html");
  }

  @Test
  @Video
  void userCanBrowseTabs_webdriver_api() {
    openFile("page_with_tabs.html");

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
  @Video
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
  @Video
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
  @Video
  void canSwitchToWindowByIndex_other_browsers_but_htmlunit() {
    assumeFalse(browser().isChrome() || browser().isHtmlUnit());
    $(byText("Page2: alerts")).click();
    $(byText("Page1: uploads")).click();
    $(byText("Page3: jquery")).click();

    $("h1").shouldHave(text("Tabs"));

    switchTo().window(1);
    $("h1").shouldHave(text("Page with alerts"));
    switchTo().window(2);
    $("h1").shouldHave(text("File uploads"));
    switchTo().window(3);
    $("h1").shouldHave(text("Page with JQuery"));
    switchTo().window(0);
    $("h1").shouldHave(text("Tabs"));
  }

  @Test
  @Video
  void canSwitchBetweenWindowsWithSameTitles() {
    assumeFalse(browser().isHtmlUnit());
    $(byText("Page4: same title")).click();
    $("h1").shouldHave(text("Tabs"));

    switchTo().window("Test::tabs::title");
    $("body").shouldHave(text("Secret phrase 1"));
    String firstHandle = driver().getWebDriver().getWindowHandle();

    switchTo().window(0);
    $("h1").shouldHave(text("Tabs"));
    $(byText("Page5: same title")).click();
    switchTo().window("Test::tabs::title");
    $("body").shouldHave(text("Secret phrase 1"));
    switchTo().window(0);
    $("h1").shouldHave(text("Tabs"));

//    switchTo().windowExceptHandles("Test::tabs::title", firstHandle); $("body").shouldHave(text("Secret phrase 2"));
//    String secondHandle = getWebDriver().getWindowHandle();
//
//    switchTo().window(0); $("h1").shouldHave(text("Tabs"));
//    $(byText("Page6: same title")).click();
//    switchTo().window("Test::tabs::title"); $("body").shouldHave(text("Secret phrase 1"));
//    switchTo().window(0); $("h1").shouldHave(text("Tabs"));
//
//    switchTo().windowExceptHandles("Test::tabs::title", firstHandle, secondHandle);
//    $("body").shouldHave(text("Secret phrase 3"));
//
//    switchTo().window("Test::tabs"); $("h1").shouldHave(text("Tabs"));
  }

  @Test
  void throwsNoSuchWindowExceptionWhenSwitchingToAbsentWindowByTitle() {
    assertThat(driver().title())
      .isEqualTo("Test::tabs");

    assertThatThrownBy(() -> {
      switchTo().window("absentWindow");
    }).isInstanceOf(NoSuchWindowException.class).hasMessage("No window found with name or handle or title: absentWindow");
  }

  @Test
  void throwsNoSuchWindowExceptionWhenSwitchingToAbsentWindowByIndex() {
    assertThat(driver().title())
      .isEqualTo("Test::tabs");

    assertThatThrownBy(() -> {
      switchTo().window(Integer.MAX_VALUE);
    }).isInstanceOf(NoSuchWindowException.class).hasMessage("No window found with index: " + Integer.MAX_VALUE);
  }

  @AfterEach
  void tearDown() {
    driver().close();
  }
}
