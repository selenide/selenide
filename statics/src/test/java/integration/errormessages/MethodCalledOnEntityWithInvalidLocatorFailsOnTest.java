package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidSelectorException;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class MethodCalledOnEntityWithInvalidLocatorFailsOnTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    givenHtml(
      "<ul>Hello to:",
      "<li class='the-expanse detective'>Miller <label>detective</label></li>",
      "<li class='the-expanse missing'>Julie Mao</li>",
      "</ul>"
    );
    Configuration.timeout = 0;
  }

  @Test
  void shouldCondition_When$Element_WithInvalidLocator() {
    SelenideElement element = $("##invalid-locator");

    assertThatThrownBy(() -> element.shouldHave(text("Miller")))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void actionWithoutWaiting_When$Element__WithInvalidLocator() {
    SelenideElement element = $("##invalid-locator");

    assertThatThrownBy(() -> element.exists())
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void shouldCondition_WhenCollectionElementByIndex_WithInvalidCollectionLocator() {
    SelenideElement element = $$("##invalid-locator").get(0);

    assertThatThrownBy(() -> element.shouldHave(text("Miller")))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void shouldCondition_WhenCollectionElementByCondition_WithInvalidCollectionLocator() {
    SelenideElement element = $$("##invalid-locator").findBy(cssClass("the-expanse"));

    assertThatThrownBy(() -> element.shouldBe(exist))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void shouldCondition_WhenInnerElement_WithInvalidInnerElementLocator() {
    SelenideElement element = $("ul").find("##invalid-locator");

    assertThatThrownBy(() -> element.shouldBe(exist))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void shouldCondition_WhenInnerElement_WithInvalidOuterElementLocator() {
    SelenideElement element = $("##invalid-locator").find(".the-expanse");

    assertThatThrownBy(() -> element.shouldBe(exactTextCaseSensitive("Miller")))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void shouldCondition_When$$Collection_WithInvalidLocator() {
    ElementsCollection collection = $$("##invalid-locator");

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("Miller", "Julie Mao")))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void actionWithoutWaiting_WhenFilteredCollection_WithInvalidLocator() {
    ElementsCollection collection = $$("##invalid-locator").filter(cssClass("the-expanse"));

    assertThatThrownBy(() -> collection.texts())
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void shouldCondition_WhenFilteredCollection_WithInvalidLocator() {
    ElementsCollection collection = $$("##invalid-locator").filter(cssClass("the-expanse"));

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("Miller", "Julie Mao")))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void shouldCondition_WhenInnerCollection_WithOuterInvalidLocator() {
    ElementsCollection collection = $("##invalid-locator").findAll("li");

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("Miller", "Julie Mao")))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }

  @Test
  void shouldCondition_WhenInnerCollection_WithInnerInvalidLocator() {
    ElementsCollection collection = $("ul").findAll("##invalid-locator");

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("Miller", "Julie Mao")))
      .isInstanceOf(InvalidSelectorException.class)
      .hasMessageContaining("##invalid-locator");
  }
}
