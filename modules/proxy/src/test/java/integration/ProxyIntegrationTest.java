package integration;

import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

public abstract class ProxyIntegrationTest extends IntegrationTest {
  @Override
  @BeforeEach
  @AfterEach
  void setUpEach() {
    resetSettings();
    turnProxy(true);
    timeout = new SelenideConfig().timeout();
    if (hasWebDriverStarted()) {
      open("about:blank");
    }
  }
}
