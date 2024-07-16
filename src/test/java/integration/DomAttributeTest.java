package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.domAttribute;
import static com.codeborne.selenide.Condition.domAttributeValue;

public final class DomAttributeTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_dom_attribute_and_property.html");
  }

  @Test
  void canVerifyDomAttributeExistence() {
    $("#grandchild_div").shouldHave(domAttribute("autofocus"));
    $("#child_div1").shouldNotHave(domAttribute("disabled"));
  }

  @Test
  void canVerifyDomAttributeValue() {
    $("#theHiddenElement").shouldHave(domAttributeValue("hidden", "true"));
    $("#child_div2").shouldNotHave(domAttributeValue("hidden", "true"));
  }
}
