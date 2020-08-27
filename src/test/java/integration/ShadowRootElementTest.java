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
    $(shadowRoot("#shadow-host (>) #inner-shadow-host", "p"))
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
      .hasMessageContaining("The element was not found: {[h1] p}");
  }

  @Test
  void throwErrorWhenInnerShadowHostAbsent() {
    assertThatThrownBy(() -> $(shadowRoot("#shadow-host (>) #nonexistent", "p")).text())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {[#shadow-host (>) #nonexistent] p}")
      .hasCauseInstanceOf(NoSuchElementException.class)
      .hasMessageContaining("The element was not found: {[#shadow-host (>) #nonexistent] p}");
  }

  @Test
  void getTargetElementsViaShadowHost() {
    $$(shadowRoot("#shadow-host", "div.test-class"))
      .shouldHaveSize(2);
  }

  @Test
  void getElementsInsideInnerShadowHost() {
    $$(shadowRoot("#shadow-host (>) #inner-shadow-host", "p"))
      .shouldHaveSize(1);
  }

  @Test
  void getNonExistingTargetElementsInsideShadowHost() {
    $$(shadowRoot("#shadow-host", "#nonexistent"))
      .shouldHaveSize(0);
  }

  @Test
  void getAllShadowHost() {
    $$(shadowRoot("(>>) *", null))
      .shouldHaveSize(14);
  }

  @Test
  void getElementsInsideInnerShadowHost2() {
    $$(shadowRoot("(>>) *", "h2"))
      .shouldHaveSize(15);
  }

  @Test
  void getElementsInsideInnerShadowHost3() {
    $$(shadowRoot("(>>) *", "p"))
      .shouldHaveSize(32);
  }

  @Test
  void getElementsInsideInnerShadowHost4() {
    $$(shadowRoot("#next-shadow-host (>) div", "h2"))
      .shouldHaveSize(5);
  }

  @Test
  void getElementsInsideInnerShadowHost5() {
    $$(shadowRoot("#next-shadow-host (>) div.fourth-shadow-host", "p"))
      .shouldHaveSize(0);
  }

  @Test
  void getElementsInsideInnerShadowHost6() {
    $$(shadowRoot("#next-shadow-host (>>) div.fourth-shadow-host", "p"))
      .shouldHaveSize(30);
  }

  @Test
  void getElementsInsideInnerShadowHost7() {
    $$(shadowRoot("#next-shadow-host (>) div (>) div.fourth-shadow-host", "p"))
      .shouldHaveSize(30);
  }

  @Test
  void getElementsInsideInnerShadowHost8() {
    $(shadowRoot("(>>) div.fourth-shadow-host", null)).$$(shadowRoot(null, "p"))
      .shouldHaveSize(3);
  }
}
