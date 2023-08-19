package integration.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.attributes;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeNotEqual;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.tagName;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class CollectionMethodsTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void useTwoDollarsToGetListOfElements() {
    $$("#radioButtons input").shouldHave(size(4));
    $$(By.cssSelector("#radioButtons input")).shouldHave(size(4));

    $("#radioButtons").$$("input").shouldHave(size(4));
    $("#radioButtons").$$(By.tagName("input")).shouldHave(size(4));
    $("#radioButtons").findAll("input").shouldHave(size(4));
    $("#radioButtons").findAll(By.tagName("input")).shouldHave(size(4));
  }

  @Test
  void invalidSelector() {
    assertThatThrownBy(() -> $$(By.xpath("//xxx[@'")).shouldHave(size(0)))
      .isInstanceOf(InvalidSelectorException.class);
  }

  @Test
  void canUseSizeMethod() {
    assertThat($$(By.name("domain"))).hasSize(1);
    assertThat($$("#theHiddenElement")).hasSize(1);
    assertThat($$("#radioButtons input")).hasSize(4);
    assertThat($$(By.xpath("//select[@name='domain']/option"))).hasSize(4);
    assertThat($$(By.name("non-existing-element"))).hasSize(0);
  }

  @Test
  void canCheckIfCollectionIsEmpty() {
    $$(By.name("#dynamic-content-container span")).shouldBe(empty);
    $$(By.name("non-existing-element")).shouldBe(empty);
    $$(byText("Loading...")).shouldBe(empty);
  }

  @Test
  void canCheckIfCollectionIsEmptyForNonExistingParent() {
    $$("not-existing-locator").first().$$("#multirowTable")
      .shouldHave(size(0))
      .shouldBe(empty)
      .shouldBe(size(0))
      .shouldBe(sizeGreaterThan(-1))
      .shouldBe(sizeGreaterThanOrEqual(0))
      .shouldBe(sizeNotEqual(1))
      .shouldBe(sizeLessThan(1))
      .shouldBe(sizeLessThanOrEqual(0));

    assertThat($$("not-existing-locator").last().$$("#multirowTable").isEmpty()).isTrue();
  }

  @Test
  void canCheckSizeOfCollection() {
    withLongTimeout(() -> {
      $$(By.name("domain")).shouldHave(size(1));
      $$("#theHiddenElement").shouldHave(size(1));
      $$("#radioButtons input").shouldHave(size(4));
      $$(By.xpath("//select[@name='domain']/option")).shouldHave(size(4));
      $$(By.name("non-existing-element")).shouldHave(size(0));
      $$("#dynamic-content-container span").shouldHave(size(2));
    });
  }

  @Test
  void shouldWaitUntilCollectionGetsExpectedSize() {
    withLongTimeout(() -> {
      ElementsCollection spans = $$("#dynamic-content-container span");

      spans.shouldHave(size(2)); // appears after 2 seconds

      assertThat(spans).hasSize(2);
      assertThat(spans.texts()).isEqualTo(asList("dynamic content", "dynamic content2"));
    });
  }

  @Test
  void canCheckThatElementsHaveCorrectTexts() {
    withLongTimeout(() -> {
      $$("#dynamic-content-container span").shouldHave(
        texts("dynamic content", "dynamic content2"),
        texts("mic cont", "content2"),
        exactTexts(asList("dynamic content", "dynamic content2")));
    });
  }

  @Test
  void canCheckCollectionAttributes() {
    $$("#domain option").shouldHave(attributes("data-mailServerId",
      "111", "222A", "33333B", "111АБВГД"));
  }

  @Test
  void userCanFilterOutMatchingElements() {
    $$("#multirowTable tr").shouldHave(size(2));
    $$("#multirowTable tr").filterBy(partialText("Norris")).shouldHave(size(1));
    $$("#multirowTable tr").filterBy(cssClass("inexisting")).shouldHave(size(0));
    $$("#multirowTable *").filterBy(tagName("tr")).shouldHave(size(2));
    $$("#multirowTable *").filterBy(tagName("td")).shouldHave(size(4));
  }

  @Test
  void userCanExcludeMatchingElements() {
    $$("#multirowTable tr").shouldHave(size(2));
    $$("#multirowTable tr").excludeWith(partialText("Chack")).shouldHave(size(0));
    $$("#multirowTable tr").excludeWith(cssClass("inexisting")).shouldHave(size(2));
    $$("table").excludeWith(tagName("table")).shouldHave(size(0));
  }

  @Test
  void errorMessageShouldShowFullAndConditionDescription() {
    ElementsCollection filteredRows = $$("#multirowTable tr")
      .filterBy(and("condition name", partialText("Chack"), partialText("Baskerville")));

    assertThatThrownBy(() -> filteredRows.shouldHave(size(0)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch")
      .hasMessageContaining("expected: = 0, actual: 1")
      .hasMessageContaining("""
        collection: #multirowTable tr.filter(condition name: partial text "Chack" and partial text "Baskerville")""")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Page source: ")
      .hasMessageContaining("Timeout: ");
  }

  @Test
  void errorMessageShouldShow_whichElementInChainWasNotFound() {
    assertThatThrownBy(() -> $$("#multirowTable").findBy(text("INVALID-TEXT"))
      .findAll("valid-selector")
      .shouldHave(texts("foo bar")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {#multirowTable.findBy(text \"INVALID-TEXT\")/valid-selector}")
      .hasMessageContaining("Timeout: 1 ms.")
      .hasCauseInstanceOf(NoSuchElementException.class)
      .getCause()
      .hasMessageStartingWith("Cannot locate an element #multirowTable.findBy(text \"INVALID-TEXT\")");
  }

  @Test
  void userCanFindMatchingElementFromList() {
    $$("#multirowTable tr").findBy(partialText("Norris")).shouldHave(text("Chack Norris"));
  }

  @Test
  void findWaitsUntilElementMatches() {
    withLongTimeout(() -> {
      $$("#dynamic-content-container span").findBy(text("dynamic content2")).shouldBe(visible);
      $$("#dynamic-content-container span").findBy(text("unexisting")).shouldNot(exist);
    });
  }

  @Test
  void collectionMethodsCanBeChained() {
    $$("#multirowTable tr").shouldHave(size(2))
      .filterBy(partialText("Norris")).shouldHave(size(1));
  }

  @Test
  void shouldMethodsCanCheckMultipleConditions() {
    $$("#multirowTable tr td").shouldHave(size(4), texts(asList("Chack", "Norris", "Chack", "L'a Baskerville")));
  }

  @Test
  void getTextsOfElements() {
    openFile("page_with_selects.html");
    assertThat($$("#number option").texts()).containsExactly("Zero", "One", "Two", "Three", "Four");
  }

  @Test
  void canGetCollectionElementByIndex() {
    $$("#radioButtons input").get(0).shouldHave(value("master"));
    $$("#radioButtons input").get(1).shouldHave(value("margarita"));
    $$("#radioButtons input").get(2).shouldHave(value("cat"));
    $$("#radioButtons input").get(3).shouldHave(value("woland"));
  }

  @Test
  void canGetCollectionFirstElement() {
    $$("#radioButtons input").first().shouldHave(value("master"));
  }

  @Test
  void canGetCollectionLastElement() {
    $$("#radioButtons input").last().shouldHave(value("woland"));
  }

  @Test
  void canFindElementsByMultipleSelectors() {
    $$(".first_row").shouldHave(size(1));
    $$(".second_row").shouldHave(size(1));
    $$(".first_row,.second_row").shouldHave(size(2));
  }

  @Test
  void canGetFirstNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option");
    collection.first(2).shouldHave(size(2));
    collection.first(10).shouldHave(size(collection.size()));

    List<String> regularSublist = $$x("//select[@name='domain']/option").asFixedIterable().stream()
      .map(SelenideElement::getText)
      .collect(toList()).subList(0, 2);

    List<String> selenideSublist = collection.first(2).asFixedIterable().stream()
      .map(SelenideElement::getText)
      .collect(toList());

    assertThat(selenideSublist).isEqualTo(regularSublist);
  }

  @Test
  void canGetElementByIndex_fromFirstNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option").first(3).shouldHave(size(3));

    collection.get(0).shouldHave(text("@one.io"));
    collection.get(1).shouldHave(text("@two.eu"));
    collection.get(2).shouldHave(text("@three.com"));
  }

  @Test
  void canGetElementByIndex_fromFirstNElements_ofFilteredCollection() {
    ElementsCollection collection = $$x("//select[@name='domain']/option")
      .filterBy(partialText(".e"))
      .first(2)
      .shouldHave(size(2));

    collection.get(0).shouldHave(text("@two.eu"));
    collection.get(1).shouldHave(text("@four.ee"));
    assertThatThrownBy(() -> collection.get(2).getText())
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessage("Index: 2, size: 2");
  }

  @Test
  void canGetLastNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option");
    collection.last(2).shouldHave(size(2));
    collection.last(10).shouldHave(size(collection.size()));

    List<String> regularSublist = $$x("//select[@name='domain']/option").asFixedIterable().stream()
      .map(SelenideElement::getText)
      .collect(toList()).subList(2, collection.size());

    List<String> selenideSublist = collection.last(2).asFixedIterable().stream()
      .map(SelenideElement::getText)
      .collect(toList());

    assertThat(selenideSublist).isEqualTo(regularSublist);
  }

  @Test
  void canGetElementByIndex_fromLastNElements_ofFilteredCollection() {
    ElementsCollection collection = $$x("//select[@name='domain']/option")
      .filterBy(partialText(".e"))
      .last(2)
      .shouldHave(size(2));

    collection.get(0).shouldHave(text("@two.eu"));
    collection.get(1).shouldHave(text("@four.ee"));

    assertThatThrownBy(() -> collection.get(2).getText())
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessage("Index: 2");
    assertThatThrownBy(() -> collection.get(3).getText())
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessage("Index: 3");
  }

  @Test
  void canChainFilterAndFirst() {
    $$("div").filterBy(visible).first()
      .shouldBe(visible)
      .shouldHave(text("non-clickable element"));

    $$("div").filterBy(visible).get(2).click();
  }

  @Test
  void errorWhenFindInLastElementOfEmptyCollection() {
    assertThatThrownBy(() -> $$("#not_exist").last().$("#multirowTable").should(exist))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist:last}")
      .hasCauseInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void collectionToString() {
    assertThat($("not-existing-locator"))
      .hasToString("{not-existing-locator}");

    assertThat($$("not-existing-locator"))
      .hasToString("[not-existing-locator]");

    assertThat($$("input[type=checkbox].red").as("red checkboxes"))
      .hasToString("red checkboxes");

    assertThat($$(".active").first(42))
      .hasToString("[.active]:first(42)");

    assertThat($$(".parent").first(2).filterBy(cssClass("child")))
      .hasToString("[.parent]:first(2).filter(css class \"child\")");
  }

  @Test
  void filteredWebElementsCollectionToString() {
    List<WebElement> webElements = driver().getWebDriver().findElements(By.cssSelector("#hero option"));
    ElementsCollection collection = driver().$$(webElements).filterBy(attribute("value"));

    assertThat(collection).hasToString("$$(5 elements).filter(attribute value)");
    assertThatThrownBy(() -> collection.shouldHave(size(999), Duration.ofMillis(0)))
      .hasMessageStartingWith("List size mismatch")
      .hasMessageContaining("expected: = 999, actual: 5")
      .hasMessageContaining("collection: $$(5 elements).filter(attribute value)");
  }

  @Test
  void filteredWebElementsCollection_singleElement_ToString() {
    List<WebElement> webElements = driver().getWebDriver().findElements(By.cssSelector("#hero option"));
    SelenideElement singleElement = driver().$$(webElements).filterBy(attribute("value")).get(2);

    assertThat(singleElement).hasToString("$$(5 elements).filter(attribute value)[2]");
    assertThatThrownBy(() -> singleElement.shouldHave(text("nope"), Duration.ofMillis(0)))
      .hasMessageStartingWith("Element should have text \"nope\" {$$(5 elements).filter(attribute value)[2]}")
      .hasMessageContaining("Element: '<option value=\"arnold \"schwarzenegger\"\">Arnold \"Schwarzenegger\"</option>'");
  }
}
