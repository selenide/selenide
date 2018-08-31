package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.Test;

public class SelenideDriverITest extends IntegrationTest {
  @Test
  void canUseTwoBrowsersInSameThread() {
    SelenideDriver browser1 = new SelenideDriver(null);
    SelenideDriver browser2 = new SelenideDriver(null);

    browser1.open("/page_with_images.html?browser=" + Configuration.browser);
    browser2.open("/page_with_selects_without_jquery.html?browser=" + Configuration.browser);

    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();

    //browser1.$("#valid-image img").shouldBe(visible);
    //browser2.$("#magic-id").shouldBe(visible);
  }
}
