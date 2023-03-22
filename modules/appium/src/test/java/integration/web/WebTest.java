package integration.web;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

public class WebTest {
  @BeforeEach
  void setUp() {
    closeWebDriver();
    Configuration.browser = "chrome";
  }

  @Test
  void canOpenWebSite() {
    open("https://selenide.org");
    $(".testimonials").scrollTo();
  }
}
