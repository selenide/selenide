package integration;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;


final class ShadowElementDeepSelectorsTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_shadow_dom.html");
  }

  @Test
  void clickInsideShadowHost() {
    SelenideElement button = $(shadowDeepCss("#buttonInShadow"));
    button.shouldHave(exactText("Button 1"));
    button.click();
    button.shouldHave(exactText("Changed Button 1"));
  }

  @Test
  void sendKeysInsideInnerShadowHost() {
    assumeThat(driver().browser().isFirefox())
      .as("Firefox doesn't support sendKeys() inside shadow dom, see https://bugzilla.mozilla.org/show_bug.cgi?id=1503860")
      .isFalse();

    SelenideElement input = $(shadowDeepCss("#inputInInnerShadow"));
    input.sendKeys("I can type text inside of shadow dom");
    input.shouldHave(exactValue("I can type text inside of shadow dom"));
  }

  @Test
  void sendKeysInsideShadowHost() {
    assumeThat(driver().browser().isFirefox())
      .as("Firefox doesn't support sendKeys() inside shadow dom, see https://bugzilla.mozilla.org/show_bug.cgi?id=1503860")
      .isFalse();
    SelenideElement input = $(shadowDeepCss("#inputInShadow"));
    input.sendKeys("I can type text inside of shadow dom");
    input.shouldHave(exactValue("I can type text inside of shadow dom"));
  }

  @Test
  void setValueInsideShadowHost() {
    SelenideElement input = $(shadowDeepCss("#inputInShadow"));
    withFastSetValue(() -> {
      input.setValue("I can type text inside of shadow dom");
      input.shouldHave(exactValue("I can type text inside of shadow dom"));
    });
  }

  @Test
  void setValueInsideInnerShadowHost() {
    SelenideElement input = $(shadowDeepCss("#inputInInnerShadow"));
    withFastSetValue(() -> {
      input.setValue("I can type text inside of shadow dom");
      input.shouldHave(exactValue("I can type text inside of shadow dom"));
    });
  }

  @Test
  void clickInsideInnerShadowHost() {
    SelenideElement button = $(shadowDeepCss("#buttonInInnerShadow"));
    button.shouldHave(exactText("Button 2"));
    button.click();
    button.shouldHave(exactText("Changed Button 2"));
  }

  @Test
  void clickInsideShadowHostInsideOfOtherShadowHost() {
    $("#shadow-host")
      .find(shadowDeepCss("#inner-shadow-host p"))
      .shouldHave(text("The Shadow-DOM inside another shadow tree"));
  }

  @Test
  void clickInsideShadowHostInsideOfElement() {
    $("body")
      .find(shadowDeepCss("#shadow-host #inner-shadow-host p"))
      .shouldHave(text("The Shadow-DOM inside another shadow tree"));
  }

  @Test
  void getTargetElementViaShadowHost() {
    $(shadowDeepCss("#shadow-host p"))
      .shouldHave(text("Inside Shadow-DOM"));
  }

  @Test
  void getElementInsideInnerShadowHost() {
    $(shadowDeepCss(".shadow-host #inner-shadow-host p"))
      .shouldHave(text("The Shadow-DOM inside another shadow tree"));
  }

  @Test
  void throwErrorWhenGetNonExistingTargetInsideShadowRoot() {
    assertThatThrownBy(() -> $(shadowDeepCss("#shadow-host#nonexistent")).text())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void throwErrorWhenShadowHostDoesNotHaveShadowRoot() {
    assertThatThrownBy(() -> $(shadowDeepCss("h1 p")).text())
      .isInstanceOf(ElementNotFound.class)
      .hasCauseInstanceOf(NoSuchElementException.class)
      .hasMessageContaining("Cannot locate an element in shadow dom By.shadowDeepCss: h1 p");
  }

  @Test
  void getTargetElementsViaShadowHost() {
    $$(shadowDeepCss("#shadow-host div.test-class"))
      .shouldHave(size(2));
  }

  @Test
  void getElementsInsideInnerShadowHost() {
    $$(shadowDeepCss(".shadow-host .inner-shadow-host p"))
      .shouldHave(size(1));
  }

  @Test
  void getNonExistingTargetElementsInsideShadowHost() {
    $$(shadowDeepCss("#shadow-host #nonexistent"))
      .shouldHave(size(0));
  }

  @Test
  void getAllElementsInAllNestedShadowHosts() {
    ElementsCollection elements = $$(shadowDeepCss(
      "#shadow-container .shadow-container-child .shadow-container-child-child .shadow-container-child-child-item")
    );
    elements.shouldHave(size(3));
    assertThat(elements.get(0).getText()).isEqualTo("shadowContainerChildChild1Host1").as("Mismatch in name of first child container");
    assertThat(elements.get(2).getText()).isEqualTo("shadowContainerChildChild1Host3").as("Mismatch in name of last child container");
  }
}
