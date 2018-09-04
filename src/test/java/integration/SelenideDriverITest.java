package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.close;

public class SelenideDriverITest extends IntegrationTest {
  private SelenideDriver browser1;
  private SelenideDriver browser2;

  @BeforeEach
  void setUp() {
    close();
  }

  @AfterEach
  void tearDown() {
    browser1.close();
    browser2.close();
  }

  @Test
  void canUseTwoBrowsersInSameThread() {
    browser1 = new SelenideDriver();
    browser2 = new SelenideDriver();

    browser1.open("/page_with_images.html?browser=" + Configuration.browser);
    browser2.open("/page_with_selects_without_jquery.html?browser=" + Configuration.browser);

    assertThat(WebDriverRunner.hasWebDriverStarted()).isFalse();

    //browser1.$("#valid-image img").shouldBe(visible);
    //browser2.$("#magic-id").shouldBe(visible);
  }
}
