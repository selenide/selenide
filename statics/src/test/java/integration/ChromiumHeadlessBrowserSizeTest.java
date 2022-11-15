package integration;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import java.time.Duration;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

final class ChromiumHeadlessBrowserSizeTest extends IntegrationTest {

  @BeforeEach
  void setUp() {
    assumeThat(isChrome() || isEdge()).isTrue();
    assumeThat(Configuration.headless).isTrue();
    closeWebDriver();
    Configuration.browserSize = "1200x1000";
    openFile("page_with_uploads.html");
  }

  @Test
  void browserWindowSizeOnNewTabTest() {
    Selenide.executeJavaScript("window.open()");
    Selenide.Wait()
      .withTimeout(Duration.ofSeconds(2))
      .until(it -> it.getWindowHandles().size() == 2);
    Selenide.switchTo().window(1);
    Dimension newTabWindowsSize = WebDriverRunner.getWebDriver().manage().window().getSize();
    assertThat(newTabWindowsSize).isEqualTo(new Dimension(1200, 1000));
  }
}
