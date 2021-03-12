package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.shadowCss;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

final class ShadowElementTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_shadow_dom.html");
  }

  @Test
  void sendKeysInsideShadowHost() {
    assumeThat(driver().browser().isFirefox())
      .as("Firefox doesn't support sendKeys() inside shadow dom, see https://bugzilla.mozilla.org/show_bug.cgi?id=1503860")
      .isFalse();
    SelenideElement input = $(shadowCss("#inputInShadow", "#shadow-host"));
    input.sendKeys("I can type text inside of shadow dom");
    input.shouldHave(exactValue("I can type text inside of shadow dom"));
  }

  @Test
  void setValueInsideShadowHost() {
    SelenideElement input = $(shadowCss("#inputInShadow", "#shadow-host"));
    withFastSetValue(() -> {
      input.setValue("I can type text inside of shadow dom");
      input.shouldHave(exactValue("I can type text inside of shadow dom"));
    });
  }

  @Test
  void sendKeysInsideInnerShadowHost() {
    assumeThat(driver().browser().isFirefox())
      .as("Firefox doesn't support sendKeys() inside shadow dom, see https://bugzilla.mozilla.org/show_bug.cgi?id=1503860")
      .isFalse();

    SelenideElement input = $(shadowCss("#inputInInnerShadow", "#shadow-host", "#inner-shadow-host"));
    input.sendKeys("I can type text inside of shadow dom");
    input.shouldHave(exactValue("I can type text inside of shadow dom"));
  }

  @Test
  void setValueInsideInnerShadowHost() {
    SelenideElement input = $(shadowCss("#inputInInnerShadow", "#shadow-host", "#inner-shadow-host"));
    withFastSetValue(() -> {
      input.setValue("I can type text inside of shadow dom");
      input.shouldHave(exactValue("I can type text inside of shadow dom"));
    });
  }

  @Test
  void clickInsideShadowHost() {
    SelenideElement button = $(shadowCss("#buttonInShadow", "#shadow-host"));
    button.shouldHave(exactText("Button 1"));
    button.click();
    button.shouldHave(exactText("Changed Button 1"));
  }

  @Test
  void clickInsideInnerShadowHost() {
    SelenideElement button = $(shadowCss("#buttonInInnerShadow", "#shadow-host", "#inner-shadow-host"));
    button.shouldHave(exactText("Button 2"));
    button.click();
    button.shouldHave(exactText("Changed Button 2"));
  }

  @Test
  void clickInsideShadowHostInsideOfElement() {
    $("#shadow-host")
      .find(shadowCss("p", "#inner-shadow-host"))
      .shouldHave(text("The Shadow-DOM inside another shadow tree"));
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
      .hasMessageContaining("Element not found {#shadow-host -> #nonexistent -> p}")
      .hasCauseInstanceOf(NoSuchElementException.class)
      .hasMessageContaining("Cannot locate an element p in shadow roots #shadow-host -> #nonexistent");
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

  @Test
  void getAllElementsInAllNestedShadowHosts() {
    ElementsCollection elements = $$(shadowCss(".shadow-container-child-child-item",
      "#shadow-container", ".shadow-container-child", ".shadow-container-child-child"));
    elements.shouldHaveSize(3);
    assertThat(elements.get(0).getText()).isEqualTo("shadowContainerChildChild1Host1").as("Mismatch in name of first child container");
    assertThat(elements.get(2).getText()).isEqualTo("shadowContainerChildChild1Host3").as("Mismatch in name of last child container");
  }
}
