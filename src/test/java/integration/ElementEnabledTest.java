package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selenide.$;

class ElementEnabledTest extends IntegrationTest {

  @BeforeEach
  void setUp() {
    openFile("page_with_disabled_elements.html");
  }

  @Test
  void canCheckIfElementIsEnabled() {
    $("#login-button").shouldNotBe(enabled);
    $("#login-button").shouldBe(disabled);

    $("#username").shouldBe(enabled);
    $("#username").shouldNotBe(disabled);
  }

  @Test
  void unexistingElementIsNotEnabled() {
    Assertions.assertThrows(ElementNotFound.class,
      () -> $("#unexisting-element").shouldBe(enabled));
  }

  @Test
  void hiddenElementIsEnabled() {
    $("#captcha").shouldBe(enabled);
    $("#captcha").shouldNotBe(disabled);
  }
}
