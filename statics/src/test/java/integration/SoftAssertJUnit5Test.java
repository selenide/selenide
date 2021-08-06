package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.SoftAssertsExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.AssertionMode.STRICT;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

@ExtendWith({SoftAssertsExtension.class})
public class SoftAssertJUnit5Test extends IntegrationTest {
  @BeforeAll
  static void setUp() {
    Configuration.assertionMode = SOFT;
    open("https://duckduckgo.com/");
    $("#soft-assert-login").shouldNot(exist);
  }

  @Test
  void blah1() {
    $("#foo").shouldNot(exist);
  }

  @Test
  void blah2() {
    $("#bar").shouldNot(exist);
  }

  @AfterAll
  static void afterAll() {
    Configuration.assertionMode = STRICT;
    $("#soft-assert-logout").shouldNot(exist);
    closeWebDriver();
  }
}
