package integration;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.shadowCss;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ShadowElementTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_shadow_dom.html");
  }

  @Test
  void clickInsideShadowHost() {
    SelenideElement button = $(shadowCss("#anyButton", "#shadow-host"));
    button.click();
    button.shouldHave(text("Changed text"));
  }

  @Test
  void getTargetElementViaShadowHost() {
    $(shadowCss("p", "#shadow-host"))
      .shouldHave(text("Inside Shadow-DOM"));
  }

  @Test
  void getElementInsideInnerShadowHost() {
    $(shadowCss("p", "#shadow-host", "#inner-shadow-host"))
      .shouldHave(text("The Shadow-DOM inside another shadow tree"));
  }

  @Test
  void throwErrorWhenGetNonExistingTargetInsideShadowRoot() {
    assertThatThrownBy(() -> $(shadowCss("#nonexistent", "#shadow-host")).text())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void throwErrorWhenBypassingShadowHost() {
    assertThatThrownBy(() -> $("p").text())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void throwErrorWhenShadowHostDoesNotHaveShadowRoot() {
    assertThatThrownBy(() -> $(shadowCss("p", "h1")).text())
      .isInstanceOf(ElementNotFound.class)
      .hasCauseInstanceOf(NoSuchElementException.class)
      .hasMessageContaining("The element is not a shadow host or has 'closed' shadow-dom mode:");
  }

  @Test
  void throwErrorWhenInnerShadowHostAbsent() {
    assertThatThrownBy(() -> $(shadowCss("p", "#shadow-host", "#nonexistent")).text())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {#shadow-host [#nonexistent] p}")
      .hasCauseInstanceOf(NoSuchElementException.class)
      .hasMessageContaining("The element was not found: #nonexistent");
  }

  @Test
  void getTargetElementsViaShadowHost() {
    $$(shadowCss("div.test-class", "#shadow-host"))
      .shouldHaveSize(2);
  }

  @Test
  void getElementsInsideInnerShadowHost() {
    $$(shadowCss("p", "#shadow-host", "#inner-shadow-host"))
      .shouldHaveSize(1);
  }

  @Test
  void getNonExistingTargetElementsInsideShadowHost() {
    $$(shadowCss("#nonexistent", "#shadow-host"))
      .shouldHaveSize(0);
  }
}
