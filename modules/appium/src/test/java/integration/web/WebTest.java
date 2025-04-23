package integration.web;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.ScrollOptions.defaultScrollOptions;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.appium.AppiumScrollOptions.up;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static java.time.Duration.ZERO;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WebTest {
  @BeforeEach
  final void setUp() {
    closeWebDriver();
    Configuration.browser = "chrome";
  }

  @Test
  void canOpenWebSite() {
    open("https://selenide.org");

    SelenideAppiumElement testimonials = $(By.cssSelector(".testimonials"));
    testimonials.scroll(defaultScrollOptions());
    testimonials.scrollTo();

    assertThatThrownBy(() -> testimonials.scroll(up()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Scrolling with options is only supported for mobile drivers");

    assertThatThrownBy(() -> $(By.cssSelector(".service-links #lang_eng")).shouldHave(text("This.Is.Web."), ZERO))
      .isInstanceOf(ElementShould.class)
      .hasMessageContaining("Element: '<a href=\"https://selenide.org/\" id=\"lang_eng\">EN</a>'");
  }
}
