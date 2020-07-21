package integration;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.shadowRoot;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ShadowRootElementTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_shadow_dom.html");
  }

  @Test
  void clickInsideShadowHost() {
    SelenideElement button = $(shadowRoot("#shadow-host", "#anyButton"));
    button.click();
    button.shouldHave(text("Changed text"));
  }

  @Test
  void getTargetElementViaShadowHost() {
    $(shadowRoot("#shadow-host", "p"))
      .shouldHave(text("Inside Shadow-DOM"));
  }

  @Test
  void getElementInsideInnerShadowHost() {
    $(shadowRoot("#shadow-host/#inner-shadow-host", "p"))
      .shouldHave(text("The Shadow-DOM inside another shadow tree"));
  }

  @Test
  void throwErrorWhenGetNonExistingTargetInsideShadowRoot() {
    assertThatThrownBy(() -> $(shadowRoot("#shadow-host", "#nonexistent")).text())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void throwErrorWhenBypassingShadowHost() {
    assertThatThrownBy(() -> $("p").text())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void throwErrorWhenShadowHostDoesNotHaveShadowRoot() {
    assertThatThrownBy(() -> $(shadowRoot("h1", "p")).text())
      .isInstanceOf(ElementNotFound.class)
      .hasCauseInstanceOf(NoSuchElementException.class)
      .hasMessageContaining("The element is not a shadow host or has 'closed' shadow-dom mode:");
  }

  @Test
  void throwErrorWhenInnerShadowHostAbsent() {
    assertThatThrownBy(() -> $(shadowRoot("#shadow-host/#nonexistent", "p")).text())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {#shadow-host [#nonexistent] p}")
      .hasCauseInstanceOf(NoSuchElementException.class)
      .hasMessageContaining("The element was not found: #nonexistent");
  }

  @Test
  void getTargetElementsViaShadowHost() {
    $$(shadowRoot("#shadow-host", "div.test-class"))
      .shouldHaveSize(2);
  }

  @Test
  void getElementsInsideInnerShadowHost() {
    $$(shadowRoot("#shadow-host/#inner-shadow-host", "p"))
      .shouldHaveSize(1);
  }

  @Test
  void getNonExistingTargetElementsInsideShadowHost() {
    $$(shadowRoot("#shadow-host", "#nonexistent"))
      .shouldHaveSize(0);
  }
}
