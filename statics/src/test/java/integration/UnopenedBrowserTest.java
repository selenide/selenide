package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.close;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UnopenedBrowserTest {
  @BeforeEach
  void givenNoOpenedBrowsers() {
    close();
  }

  @Test
  void dollarShouldNotOpenBrowser() {
    assertThatThrownBy(() ->
      $("div").shouldBe(visible)
    ).isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread")
      .hasMessageEndingWith("You need to call open(url) first.");
  }

  @Test
  void dollarsShouldNotOpenBrowser() {
    assertThatThrownBy(() ->
      $$("div").shouldHave(size(666))
    ).isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("No webdriver is bound to current thread")
      .hasMessageEndingWith("You need to call open(url) first.");
  }
}
