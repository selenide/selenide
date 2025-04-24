package integration.collections;

import com.codeborne.selenide.ex.AttributesMismatch;
import com.codeborne.selenide.ex.ElementNotFound;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.attributes;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AttributesTest extends ITest {
  @BeforeEach
  void openPage() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void canCheckThatElementsHaveExactlyCorrectAttributes() {
    $$(".element").shouldHave(attributes("data-value", "1 uno", "2 duo", "3 trio"));
  }

  @Test
  void canCheckBooleanValues_required() {
    $$(".field").shouldHave(attributes("required", "true", "true", "true"));
    $$(".element").shouldHave(attributes("required", null, null, null));
  }

  @Test
  void canCheckBooleanValues_readOnly() {
    $$(".field").shouldHave(attributes("readOnly", "true", "true", "true"));
    $$(".element").shouldHave(attributes("readOnly", null, null, null));
    $$("h1").shouldHave(attributes("readOnly", null, "true", "true"));
  }

  @Test
  void canCheckCssClasses() {
    $$(".element").shouldHave(attributes("class",
      "element first",
      "element middle",
      "element last"
    ));
  }

  @Test
  void canCheckProperties() {
    $$(".element").shouldHave(attributes("value", "11111", "22222", "33333"));
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $$(".element").shouldHave(attributes("data-value", "1  uno", "2  duo", "3  trio")))
      .isInstanceOf(AttributesMismatch.class)
      .hasMessageStartingWith("Attribute \"data-value\" values mismatch (#0 expected: \"1  uno\", actual: \"1 uno\")")
      .hasMessageContaining("Actual (3): [1 uno, 2 duo, 3 trio]")
      .hasMessageContaining("Expected (3): [1  uno, 2  duo, 3  trio]")
      .hasMessageContaining("Collection: .element")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void attributesCheckThrowsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").shouldHave(attributes("data-value", "1 uno", "2 duo", "3 trio")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.non-existing-elements}")
      .hasMessageContaining("Expected: Attribute: 'data-value' values [1 uno, 2 duo, 3 trio]")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: 1 ms.");
  }

  @Test
  void attributesCheckThrowsAttributesMismatchIfAttributeNotExist() {
    assertThatThrownBy(() -> $$(".element").shouldHave(attributes("not-existing-attribute", "1 uno", "2 duo", "3 trio")))
      .isInstanceOf(AttributesMismatch.class)
      .hasMessageStartingWith("Attribute \"not-existing-attribute\" values mismatch")
      .hasMessageContaining("Actual (3): [null, null, null]")
      .hasMessageContaining("Expected (3): [1 uno, 2 duo, 3 trio]");
  }
}
