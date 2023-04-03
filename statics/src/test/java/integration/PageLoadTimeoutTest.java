package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static org.assertj.core.api.Assertions.assertThat;

public class PageLoadTimeoutTest extends IntegrationTest {
  @BeforeEach
  @AfterEach
  void setUp() {
    closeWebDriver();
  }

  @Test
  void canChangeTimeout() {
    Configuration.pageLoadTimeout = 22_222;
    openFile("page_with_selects_without_jquery.html");
    assertThat(pageLoadTimeout())
      .isEqualTo(22_222);
  }

  @Test
  void dontChangeTimeoutIfNegative() {
    Configuration.pageLoadTimeout = -1L;
    openFile("page_with_selects_without_jquery.html");
    assertThat(pageLoadTimeout())
      .describedAs("300000L is default page load timeout value from Selenium")
      .isEqualTo(300000L);
  }

  private long pageLoadTimeout() {
    return WebDriverRunner.getWebDriver().manage().timeouts().getPageLoadTimeout().toMillis();
  }
}
