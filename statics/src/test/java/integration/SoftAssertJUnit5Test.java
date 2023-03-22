package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.SoftAssertsExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
  @BeforeEach
  void setUp() {
    Configuration.assertionMode = SOFT;
    open("about:blank");
    $("#soft-assert-login").shouldNot(exist);
  }

  @Test
  void blah1() {
    $("#foo").shouldNot(exist);
  }

  @Test
  void blah2() {
    $("#zoo").shouldNot(exist);

    // uncomment to trigger test failure
    // $("#foo").should(exist);
    // $("#bar").should(exist);
  }

  @AfterEach
  void tearDown() {
    // uncomment to trigger test failure
    // $("#foo").should(exist);
    // $("#bar").should(exist);
  }

  @AfterAll
  static void afterAll() {
    Configuration.assertionMode = STRICT;
    $("#soft-assert-logout").shouldNot(exist);
    closeWebDriver();

    // uncomment to trigger test failure
    // assertThat("foo").isEqualTo("bar");
  }
}
