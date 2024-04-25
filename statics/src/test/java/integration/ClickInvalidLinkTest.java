package integration;

import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

class ClickInvalidLinkTest extends IntegrationTest {

  @BeforeEach
  void setUp() {
    timeout = 100;
    openFile("page_with_invalid_link.html");
  }

  @Test
  void seleniumClick_nonFirefox() {
    assumeThat(isFirefox()).isFalse();

    getWebDriver().findElement(By.id("invalid-link")).click();
  }

  @Test
  void seleniumClick_firefox() {
    assumeThat(isFirefox()).isTrue();

    assertThatThrownBy(() -> {
      getWebDriver().findElement(By.id("invalid-link")).click();
    }).isInstanceOf(WebDriverException.class)
      .hasMessageStartingWith("Reached error page: about:neterror");
  }

  @Test
  void selenideClick_nonFirefox() {
    assumeThat(isFirefox()).isFalse();
    $("#invalid-link").click();
  }

  @Test
  void selenideClick_firefox() {
    assumeThat(isFirefox()).isTrue();

    assertThatThrownBy(() -> {
      $("#invalid-link").click();
    }).isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("WebDriverException: Reached error page: about:neterror")
      .cause()
      .isInstanceOf(WebDriverException.class)
      .hasMessageStartingWith("Reached error page: about:neterror");
  }
}
