package integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class ProxyIntegrationTest extends IntegrationTest {
  @BeforeEach
  @AfterEach
  void setUpEach() {
    resetSettings();
    turnProxy(true);
  }
}
