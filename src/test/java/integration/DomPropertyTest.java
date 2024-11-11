package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.domProperty;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public final class DomPropertyTest extends ITest {
  private final SelenideElement input = $("#name");
  private final SelenideElement button = $("#counter");

  @BeforeEach
  void openTestPage() {
    setTimeout(500);
    openFile("page_with_dom_attribute_and_property.html");
  }

  @Test
  void canVerifyDomPropertyExistence() {
    button.shouldHave(domProperty("id"));
    button.shouldNotHave(domProperty("data-absent"));
    button.shouldNotHave(domProperty("data-counter"));

    button.click();

    button.shouldHave(domProperty("data-absent"));
    button.shouldNotHave(domProperty("data-counter"));
  }

  @Test
  void canVerifyDomPropertyValue() {
    button.shouldHave(domProperty("data-counter", null));
    button.shouldNotHave(domProperty("data-absent", "22"));
    button.shouldNotHave(domProperty("data-counter", "100"));

    button.click();

    button.shouldHave(domProperty("data-counter", null));
    button.shouldHave(domProperty("data-absent", "none"));
  }

  @Test
  void canGetDomProperty() throws InterruptedException {
    assertThat(button.getDomProperty("data-absent")).isNull();
    assertThat(button.getDomProperty("data-counter")).isNull();

    button.click();
    sleep(200);

    assertThat(button.getDomProperty("data-absent")).isEqualTo("none");
    assertThat(button.getDomProperty("data-counter")).isNull();
  }

  @Test
  void domProperty_disabled() {
    input.shouldHave(domProperty("disabled"));
    input.shouldHave(domProperty("disabled", "true"));
    button.click();
    input.shouldHave(domProperty("disabled"));
    input.shouldHave(domProperty("disabled", "false"));
  }
}
