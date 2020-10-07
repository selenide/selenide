package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementWithTextNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.ex.MatcherError;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.codeborne.selenide.CollectionCondition.allMatch;
import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.noneMatch;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeNotEqual;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exist;
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
    assertThat($$(By.name("domain")))
      .hasSize(1);
    assertThat($$("#theHiddenElement"))
      .hasSize(1);
    assertThat($$("#radioButtons input"))
      .hasSize(4);
    assertThat($$(By.xpath("//select[@name='domain']/option")))
      .hasSize(4);
    assertThat($$(By.name("non-existing-element")))
      .hasSize(0);
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
      .shouldHaveSize(0)
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
      $$(By.name("domain")).shouldHaveSize(1);
      $$("#theHiddenElement").shouldHaveSize(1);
      $$("#radioButtons input").shouldHaveSize(4);
      $$(By.xpath("//select[@name='domain']/option")).shouldHaveSize(4);
      $$(By.name("non-existing-element")).shouldHaveSize(0);
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
  void ignoresWhitespacesInTexts() {
    withLongTimeout(() -> {
      $$("#dynamic-content-container span").shouldHave(
        texts("   dynamic \ncontent ", "dynamic \t\t\tcontent2\t\t\r\n"),
        exactTexts("dynamic \t\n content\n\r", "    dynamic content2      "));
    });
  }

  @Test
  void canCheckThatElementsHaveExactlyCorrectTexts() {
    withLongTimeout(() -> {
      assertThatThrownBy(() -> $$("#dynamic-content-container span").shouldHave(exactTexts("content", "content2")))
        .isInstanceOf(TextsMismatch.class);
    });
  }

  @Test
  void textsCheckThrowsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").shouldHave(texts("content1", "content2")))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void exactTextsCheckThrowsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").shouldHave(exactTexts("content1", "content2")))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void textsCheckThrowsTextsSizeMismatch() {
    withLongTimeout(() -> {
      assertThatThrownBy(() -> $$("#dynamic-content-container span")
        .shouldHave(texts("static-content1", "static-content2", "dynamic-content1")))
        .isInstanceOf(TextsSizeMismatch.class);
    });
  }

  @Test
  void textCheckThrowsTextsMismatch() {
    withLongTimeout(() -> {
      assertThatThrownBy(() -> $$("#dynamic-content-container span").shouldHave(texts("static-content1", "static-content2")))
        .isInstanceOf(TextsMismatch.class);
    });
  }

  @Test
  void failsFast_ifNoExpectedTextsAreGiven() {
    assertThatThrownBy(() -> $$("#dynamic-content-container span").shouldHave(texts()))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void userCanFilterOutMatchingElements() {
    $$("#multirowTable tr").shouldHaveSize(2);
    $$("#multirowTable tr").filterBy(text("Norris")).shouldHaveSize(1);
    $$("#multirowTable tr").filterBy(cssClass("inexisting")).shouldHaveSize(0);
  }

  @Test
  void userCanExcludeMatchingElements() {
    $$("#multirowTable tr").shouldHaveSize(2);
    $$("#multirowTable tr").excludeWith(text("Chack")).shouldHaveSize(0);
    $$("#multirowTable tr").excludeWith(cssClass("inexisting")).shouldHaveSize(2);
  }

  @Test
  void errorMessageShouldShow_whichElementInChainWasNotFound() {
    assertThatThrownBy(() -> $$("#multirowTable").findBy(text("INVALID-TEXT"))
      .findAll("valid-selector")
      .shouldHave(texts("foo bar")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {#multirowTable.findBy(text 'INVALID-TEXT')}");
  }

  @Test
  void userCanFindMatchingElementFromList() {
    $$("#multirowTable tr").findBy(text("Norris")).shouldHave(text("Norris"));
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
      .filterBy(text("Norris")).shouldHave(size(1));
  }

  @Test
  void shouldMethodsCanCheckMultipleConditions() {
    $$("#multirowTable tr td").shouldHave(size(4), texts(asList("Chack", "Norris", "Chack", "L'a Baskerville")));
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
  void canIterateCollection_withIterator() {
    Iterator<SelenideElement> it = $$("[name=domain] option").iterator();
    assertThat(it.hasNext())
      .isTrue();
    it.next().shouldHave(text("@livemail.ru"));

    assertThat(it.hasNext())
      .isTrue();
    it.next().shouldHave(text("@myrambler.ru"));

    assertThat(it.hasNext())
      .isTrue();
    it.next().shouldHave(text("@rusmail.ru"));

    assertThat(it.hasNext())
      .isTrue();
    it.next().shouldHave(text("@мыло.ру"));

    assertThat(it.hasNext())
      .isFalse();
  }

  @Test
  void canIterateCollection_withListIterator() {
    ListIterator<SelenideElement> it = $$("[name=domain] option").listIterator(3);
    assertThat(it.hasNext())
      .isTrue();
    assertThat(it.hasPrevious())
      .isTrue();
    it.previous().shouldHave(text("@rusmail.ru"));

    assertThat(it.hasPrevious())
      .isTrue();
    it.previous().shouldHave(text("@myrambler.ru"));

    assertThat(it.hasPrevious())
      .isTrue();
    it.previous().shouldHave(text("@livemail.ru"));

    assertThat(it.hasPrevious())
      .isFalse();

    it.next().shouldHave(text("@livemail.ru"));
    assertThat(it.hasPrevious())
      .isTrue();
  }

  @Test
  void canGetFirstNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option");
    collection.first(2).shouldHaveSize(2);
    collection.first(10).shouldHaveSize(collection.size());

    List<String> regularSublist = $$x("//select[@name='domain']/option").stream()
      .map(SelenideElement::getText)
      .collect(toList()).subList(0, 2);

    List<String> selenideSublist = collection.first(2).stream()
      .map(SelenideElement::getText)
      .collect(toList());

    assertThat(selenideSublist).isEqualTo(regularSublist);
  }

  @Test
  void canGetElementByIndex_fromFirstNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option").first(3).shouldHave(size(3));

    collection.get(0).shouldHave(text("@livemail.ru"));
    collection.get(1).shouldHave(text("@myrambler.ru"));
    collection.get(2).shouldHave(text("@rusmail.ru"));
  }

  @Test
  void canGetElementByIndex_fromFirstNElements_ofFilteredCollection() {
    ElementsCollection collection = $$x("//select[@name='domain']/option")
      .filterBy(text(".ru"))
      .first(2)
      .shouldHave(size(2));

    collection.get(0).shouldHave(text("@livemail.ru"));
    collection.get(1).shouldHave(text("@myrambler.ru"));
    assertThatThrownBy(() -> collection.get(2).getText())
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessage("Index: 2, size: 2");
  }

  @Test
  void canGetLastNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option");
    collection.last(2).shouldHaveSize(2);
    collection.last(10).shouldHaveSize(collection.size());

    List<String> regularSublist = $$x("//select[@name='domain']/option").stream()
      .map(SelenideElement::getText)
      .collect(toList()).subList(2, collection.size());

    List<String> selenideSublist = collection.last(2).stream()
      .map(SelenideElement::getText)
      .collect(toList());

    assertThat(selenideSublist).isEqualTo(regularSublist);
  }

  @Test
  void canGetElementByIndex_fromLastNElements_ofFilteredCollection() {
    ElementsCollection collection = $$x("//select[@name='domain']/option")
      .filterBy(text(".ru"))
      .last(2)
      .shouldHave(size(2));

    collection.get(0).shouldHave(text("@myrambler.ru"));
    collection.get(1).shouldHave(text("@rusmail.ru"));
    assertThatThrownBy(() -> collection.get(2).getText())
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessage("Index: 3");
    assertThatThrownBy(() -> collection.get(3).getText())
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessage("Index: 4");
  }

  @Test
  void canChainFilterAndFirst() {
    $$("div").filterBy(visible).first()
      .shouldBe(visible)
      .shouldHave(text("non-clickable element"));

    $$("div").filterBy(visible).get(2).click();
  }

  @Test
  void shouldThrow_ElementNotFound_causedBy_IndexOutOfBoundsException_first() {
    ElementsCollection elementsCollection = $$("not-existing-locator").first().$$("#multirowTable");
    String description = "Check throwing ElementNotFound for %s";

    assertThatThrownBy(() -> elementsCollection.shouldHaveSize(1))
      .as(description, "shouldHaveSize").isInstanceOf(ListSizeMismatch.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(size(1)))
      .as(description, "size").isInstanceOf(ListSizeMismatch.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeGreaterThan(0)))
      .as(description, "sizeGreaterThan").isInstanceOf(ListSizeMismatch.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeGreaterThanOrEqual(1)))
      .as(description, "sizeGreaterThanOrEqual").isInstanceOf(ListSizeMismatch.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeNotEqual(0)))
      .as(description, "sizeNotEqual").isInstanceOf(ListSizeMismatch.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeLessThan(0)))
      .as(description, "sizeLessThan").isInstanceOf(ListSizeMismatch.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeLessThanOrEqual(-1)))
      .as(description, "sizeLessThanOrEqual").isInstanceOf(ListSizeMismatch.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(exactTexts("any text")))
      .as(description, "exactTexts").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(texts("any text")))
      .as(description, "texts").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(itemWithText("any text")))
      .as(description, "itemWithText").isInstanceOf(ElementWithTextNotFound.class)
      .hasCauseExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void shouldThrow_ElementNotFound_causedBy_IndexOutOfBoundsException() {
    ElementsCollection elementsCollection = $$("not-existing-locator").get(1).$$("#multirowTable");
    String description = "Check throwing ElementNotFound for %s";

    assertThatThrownBy(() -> elementsCollection.shouldHaveSize(1))
      .as(description, "shouldHaveSize").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(size(1)))
      .as(description, "size").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeGreaterThan(0)))
      .as(description, "sizeGreaterThan").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeGreaterThanOrEqual(1)))
      .as(description, "sizeGreaterThanOrEqual").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeNotEqual(0)))
      .as(description, "sizeNotEqual").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeLessThan(0)))
      .as(description, "sizeLessThan").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeLessThanOrEqual(-1)))
      .as(description, "sizeLessThanOrEqual").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(exactTexts("any text")))
      .as(description, "exactTexts").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(texts("any text")))
      .as(description, "texts").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(itemWithText("any text")))
      .as(description, "itemWithText").isInstanceOf(ElementNotFound.class)
      .hasCauseExactlyInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void errorWhenFindInLastElementOfEmptyCollection() {
    assertThatThrownBy(() -> $$("#not_exist").last().$("#multirowTable").should(exist))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist:last}")
      .hasCauseInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void errorWhenFindCollectionInLastElementOfEmptyCollection() {
    assertThatThrownBy(() -> $$("#not_exist").last().$$("#multirowTable").shouldHaveSize(1))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#not_exist:last/#multirowTable}")
      .hasCauseInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void shouldHaveZeroSizeWhenFindCollectionInLastElementOfEmptyCollection() {
    $$("#not_exist").last().$$("#multirowTable").shouldHaveSize(0);
  }

  @Test
  void shouldHaveZeroSizeWhenFindCollectionInLastElementOfFullCollection() {
    $$("#user-table td").last().$$("#not_exist").shouldHaveSize(0);
  }

  @Test
  void shouldAnyMatchPredicate() {
    $$("#radioButtons input")
      .shouldBe(anyMatch("value==cat",
        el -> el.getAttribute("value").equals("cat")));
  }

  @Test
  void errorWhenAnyNotMatchedButShouldBe() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldBe(anyMatch("value==dog",
      el -> el.getAttribute("value").equals("dog"))))
      .isInstanceOf(MatcherError.class)
      .hasMessageContaining(String.format("Collection matcher error" +
        "%nExpected: any of elements to match [value==dog] predicate"));
  }

  @Test
  void shouldAllMatchPredicate() {
    $$("#radioButtons input")
      .shouldBe(allMatch("name==me",
        el -> el.getAttribute("name").equals("me")));
  }

  @Test
  void errorWhenAllNotMatchedButShouldBe() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldBe(allMatch("value==cat",
      el -> el.getAttribute("value").equals("cat"))))
      .isInstanceOf(MatcherError.class)
      .hasMessageContaining(String.format("Collection matcher error" +
        "%nExpected: all of elements to match [value==cat] predicate"));
  }

  @Test
  void shouldNoneMatchPredicate() {
    $$("#radioButtons input")
      .shouldBe(noneMatch("name==you",
        el -> el.getAttribute("name").equals("you")));
  }

  @Test
  void errorWhenSomeMatchedButNoneShould() {
    assertThatThrownBy(() -> $$("#radioButtons input").shouldBe(noneMatch("value==cat",
      el -> el.getAttribute("value").equals("cat"))))
      .isInstanceOf(MatcherError.class)
      .hasMessageContaining(String.format("Collection matcher error" +
        "%nExpected: none of elements to match [value==cat] predicate"));
  }

  @Test
  void shouldItemWithText() {
    $$("#user-table tbody tr td.firstname")
      .shouldBe(itemWithText("Bob"));
  }

  @Test
  void errorWhenItemWithTextNotMatchedButShouldBe() {
    String expectedText = "Luis";
    assertThatThrownBy(()  -> $$("#user-table tbody tr td.firstname").shouldHave(itemWithText(expectedText)))
      .isInstanceOf(ElementWithTextNotFound.class)
      .hasMessageContaining(String.format("Element with text not found" +
        "%nActual: %s" +
        "%nExpected: %s", Arrays.asList("Bob", "John"), Collections.singletonList(expectedText)));
  }
}
