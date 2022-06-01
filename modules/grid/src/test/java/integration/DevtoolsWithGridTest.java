package integration;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class DevtoolsWithGridTest extends AbstractGridTest {
  @BeforeEach
  void openBrowser() {
    assumeThat(isChrome()).isTrue();
    Configuration.remote = "http://localhost:" + hubPort + "/wd/hub";
    open("/start_page.html");
  }

  @Test
  void userCanGetDevtoolsFromGrid() {
    try (var devtools = Selenide.devTools().seleniumDevTools()) {
      assertThat(devtools.getCdpSession()).isNotNull();
    }
  }
}
