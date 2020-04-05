package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.Test;

class BrowserOptionsInterceptorSampleUsageTest extends IntegrationTest {
  @Test
  void canModifyOptionsByConfig() {
    Configuration.browserOptionsInterceptors.chromeOptionsInterceptor = options -> {
      options.addArguments("--eat-less-memory");
    };
    Configuration.browserOptionsInterceptors.firefoxOptionsInterceptor = options -> {
      options.addPreference("firefox", "my awesome preference");
    };
    openFile("page_with_big_divs.html");
  }
}
