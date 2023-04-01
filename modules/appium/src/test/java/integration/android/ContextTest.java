package integration.android;

import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static org.assertj.core.api.Assertions.assertThat;

class ContextTest extends BaseSwagLabsAndroidTest {

  @BeforeEach
  void setUp() {
    closeWebDriver();
  }

  @Test
  void contexts() {
    SelenideAppium.openAndroidDeepLink("mydemoapprn://webview", "com.saucelabs.mydemoapp.rn");
    SelenideAppium.setContext("NATIVE_APP");
    assertThat(SelenideAppium.getContextHandles())
      .anyMatch(context -> context.contentEquals("NATIVE_APP"));
  }
}
