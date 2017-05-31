package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selenide.$;

public class ElementEnabledTest extends IntegrationTest {

  @Before
  public void setUp() {
    openFile("page_with_disabled_elements.html");
  }

  @Test
  public void canCheckIfElementIsEnabled() {
    $("#login-button").shouldNotBe(enabled);
    $("#login-button").shouldBe(disabled);

    $("#username").shouldBe(enabled);
    $("#username").shouldNotBe(disabled);
  }

  @Test(expected = ElementNotFound.class)
  public void unexistingElementIsNotEnabled() {
    $("#unexisting-element").shouldBe(enabled);
  }

  @Test
  public void hiddenElementIsEnabled() {
    $("#captcha").shouldBe(enabled);
    $("#captcha").shouldNotBe(disabled);
  }
}
