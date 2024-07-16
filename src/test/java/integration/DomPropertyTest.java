package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.domProperty;
import static com.codeborne.selenide.Condition.domPropertyValue;

public final class DomPropertyTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_dom_attribute_and_property.html");
  }

  @Test
  void canVerifyDomAttributeExistence() {
    $("#grandchild_div").shouldHave(domProperty("autofocus"));
    $("#child_div1").shouldNotHave(domProperty("class"));
  }

  @Test
  void canVerifyDomAttributeValue() {
    $("#theHiddenElement").shouldHave(domPropertyValue("id", "theHiddenElement"));
    $("#child_div2").shouldNotHave(domPropertyValue("hidden", "hidden"));
  }
}
