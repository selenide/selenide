package integration.web;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.appium.AppiumScrollOptions.up;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WebTest {
  @BeforeEach
  void setUp() {
    closeWebDriver();
    Configuration.browser = "chrome";
  }

  @Test
  void canOpenWebSite() {
    open("https://selenide.org");

    SelenideAppiumElement testimonials = $(By.cssSelector(".testimonials"));
    testimonials.scrollTo();

    assertThatThrownBy(() -> testimonials.scroll(up()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Scrolling with options is only supported for mobile drivers");
  }
}
