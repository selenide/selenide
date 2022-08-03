package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.WindowType.WINDOW;

final class WindowsTest extends IntegrationTest {
  @BeforeAll
  @AfterAll
  static void closePreviousBrowser() {
    closeWebDriver();
  }

  @BeforeEach
  void setUp() {
    Configuration.browserSize = "1600x400";
    Configuration.browserPosition = "13x99";
    openFile("page_with_tabs.html");
  }

  @Test
  void opensNewWindowWithSameSizeAndPosition() {
    assertThat(getWebDriver().getWindowHandles()).hasSize(1);
    assertThat(getWebDriver().getTitle()).isEqualTo("Test::tabs");
    assertThat(getWebDriver().manage().window().getSize()).isEqualTo(new Dimension(1600, 400));
    assertThat(getWebDriver().manage().window().getPosition()).isEqualTo(new Point(13, 99));

    Selenide.switchTo().newWindow(WINDOW);

    assertThat(getWebDriver().getWindowHandles()).hasSize(2);
    assertThat(getWebDriver().getTitle()).isEqualTo("");
    assertThat(getWebDriver().manage().window().getSize()).isEqualTo(new Dimension(1600, 400));
    assertThat(getWebDriver().manage().window().getPosition()).isEqualTo(new Point(13, 99));

    Selenide.closeWindow();
    assertThat(getWebDriver().getWindowHandles()).hasSize(1);

    Selenide.switchTo().window(0);
    assertThat(getWebDriver().getTitle()).isEqualTo("Test::tabs");
    assertThat(getWebDriver().manage().window().getSize()).isEqualTo(new Dimension(1600, 400));
    assertThat(getWebDriver().manage().window().getPosition()).isEqualTo(new Point(13, 99));
  }
}
