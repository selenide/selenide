package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.domAttribute;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public final class DomAttributeTest extends ITest {
  private final SelenideElement input = $("#name");
  private final SelenideElement button = $("#counter");

  @BeforeEach
  void openTestPage() {
    setTimeout(500);
    openFile("page_with_dom_attribute_and_property.html");
  }

  @Test
  void canVerifyDomAttributeExistence() {
    button.shouldNotHave(attribute("data-absent"));
    button.shouldNotHave(domAttribute("data-absent"));

    button.shouldHave(attribute("data-counter"));
    button.shouldHave(domAttribute("data-counter"));

    button.click();

    button.shouldHave(attribute("data-absent"));
    button.shouldNotHave(domAttribute("data-absent"));

    button.shouldHave(attribute("data-counter"));
    button.shouldHave(domAttribute("data-counter"));
  }

  @Test
  void canVerifyDomAttributeValue() {
    button.shouldNotHave(attribute("data-absent", "22"));
    button.shouldNotHave(domAttribute("data-absent", "22"));

    button.shouldHave(attribute("data-counter", "100"));
    button.shouldHave(domAttribute("data-counter", "100"));

    button.click();
    button.click();
    button.click();

    button.shouldHave(attribute("data-counter", "103"));
    button.shouldHave(domAttribute("data-counter", "103"));

    button.shouldHave(attribute("data-absent", "none"));
    button.shouldHave(domAttribute("data-absent", null));
  }

  @Test
  void canGetDomAttribute() throws InterruptedException {
    assertThat(button.getDomAttribute("data-absent")).isNull();
    assertThat(button.getDomAttribute("data-counter")).isEqualTo("100");

    button.click();
    button.click();
    sleep(200);

    assertThat(button.getDomAttribute("data-counter")).isEqualTo("102");
  }

  @Test
  void domAttribute_disabled() {
    input.shouldHave(domAttribute("disabled"));
    input.shouldHave(domAttribute("disabled", "true"));
    button.click();
    input.shouldNotHave(domAttribute("disabled"));
    input.shouldNotHave(domAttribute("disabled", "true"));
  }
}
