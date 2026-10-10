package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static com.codeborne.selenide.Browsers.HTMLUNIT;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static org.assertj.core.api.Assertions.assertThat;

final class HtmlUnitTest extends BaseIntegrationTest {
  private SelenideDriver htmlunit;

  @BeforeEach
  void setUp() {
    htmlunit = new SelenideDriver(new SelenideConfig().browser(HTMLUNIT).baseUrl(getBaseUrl()));
  }

  @AfterEach
  void tearDown() {
    htmlunit.close();
  }

  @Test
  void createsHtmlUnitDriver() {
    htmlunit.open("/page_with_double_clickable_button.html");
    assertThat(htmlunit.getWebDriver()).isInstanceOf(HtmlUnitDriver.class);
    assertThat(htmlunit.title()).isEqualTo("Test::double-clickable-button");
  }

  @Test
  void canClickWithJavaScriptEnabled() {
    htmlunit.open("/page_with_double_clickable_button.html");
    htmlunit.$("#double-clickable-button").click();
    htmlunit.$("h2").shouldHave(text("Status: clicked"));
  }

  @Test
  void canTypeText() {
    htmlunit.open("/page_with_selects_without_jquery.html");
    htmlunit.$("#username").setValue("john");
    htmlunit.$("#password").click();
    htmlunit.$("#username").shouldHave(value("john"));
    htmlunit.$("#username-mirror").shouldHave(text("john"));
  }
}
