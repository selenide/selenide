package integration;

import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.WebDriverConditions.url;
import static com.codeborne.selenide.WebDriverConditions.urlStartingWith;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrTest extends ITest {
  @BeforeEach
  void setUp() {
    driver().open("about:blank" + testName());
  }

  @Test
  void positive_first() {
    driver().webdriver().shouldHave(urlStartingWith("about:blank").or(url("bar")));
  }

  @Test
  void positive_second() {
    driver().webdriver().shouldHave(url("foo").or(urlStartingWith("about:blank")));
  }

  @Test
  void positive_composite() {
    driver().webdriver().shouldHave(url("foo").or(url("bar")).or(urlStartingWith("about:blank")));
  }

  @Test
  void errorMessage() {
    setTimeout(2);
    assertThatThrownBy(() -> driver().webdriver().shouldHave(url("foo:foo").or(url("bar:bar"))))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("webdriver should have url foo:foo or url bar:bar")
      .hasMessageContaining("Actual value: about:blank" + testName())
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 2ms");
  }

  @Test
  void errorMessage_negative() {
    setTimeout(3);
    assertThatThrownBy(() -> driver().webdriver().shouldNotHave(url("foo:foo").or(urlStartingWith("about:blank"))))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("webdriver should not have url foo:foo or url starting with about:blank")
      .hasMessageContaining("Actual value: about:blank" + testName())
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 3ms");
  }
}
