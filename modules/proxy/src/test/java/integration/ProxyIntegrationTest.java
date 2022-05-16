package integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

public abstract class ProxyIntegrationTest extends IntegrationTest {
  @Override
  @BeforeEach
  @AfterEach
  void setUpEach() {
    resetSettings();
    turnProxy(true);
    if (hasWebDriverStarted()) {
      open("about:blank");
    }
  }
}
