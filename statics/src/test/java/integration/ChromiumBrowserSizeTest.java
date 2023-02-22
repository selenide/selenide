package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import java.time.Duration;

import static com.codeborne.selenide.WebDriverRunner.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.data.Offset.offset;

final class ChromiumBrowserSizeTest extends IntegrationTest {

  @BeforeEach
  void setUp() {
    assumeThat(isChrome() || isEdge()).isTrue();
    closeWebDriver();
    Configuration.browserSize = "900x600";
    openFile("page_with_uploads.html");
  }

  @AfterAll
  static void afterAll() {
    closeWebDriver();
  }

  @Test
  void browserWindowSizeOnNewTabTest() {
    Selenide.executeJavaScript("window.open()");
    Selenide.Wait()
      .withTimeout(Duration.ofSeconds(2))
      .until(it -> it.getWindowHandles().size() == 2);
    Selenide.switchTo().window(1);
    Dimension newTabWindowsSize = WebDriverRunner.getWebDriver().manage().window().getSize();
    assertThat(newTabWindowsSize.width).isCloseTo(900, offset(10));
    assertThat(newTabWindowsSize.height).isCloseTo(600, offset(10));
  }
}
