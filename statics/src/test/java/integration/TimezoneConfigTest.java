package integration;

import com.codeborne.selenide.Configuration;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.openqa.selenium.WindowType.TAB;
import static org.openqa.selenium.WindowType.WINDOW;

final class TimezoneConfigTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeThat(isChrome() || isEdge() || isFirefox()).isTrue();
    assumeThat(Configuration.remote).as("TZ is set for local browsers only").isNull();
    assumeThat(SystemUtils.IS_OS_WINDOWS).as("Browsers on Windows ignore TZ").isFalse();
    closeWebDriver();
    Configuration.timezone = "Asia/Tokyo";
  }

  @AfterEach
  void tearDown() {
    closeWebDriver();
    Configuration.timezone = null;
  }

  @Test
  void appliesTimezoneToAllTabsAndWindows() {
    openFile("page_with_clock.html");
    $("#timezone").shouldHave(text("Asia/Tokyo"));

    switchTo().newWindow(TAB);
    openFile("page_with_clock.html");
    $("#timezone").shouldHave(text("Asia/Tokyo"));

    switchTo().newWindow(WINDOW);
    openFile("page_with_clock.html");
    $("#timezone").shouldHave(text("Asia/Tokyo"));
  }
}
