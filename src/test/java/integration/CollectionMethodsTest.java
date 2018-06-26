package integration;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.getElements;
import static java.util.Arrays.asList;

class CollectionMethodsTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void useTwoDollarsToGetListOfElements() {
    $$("#radioButtons input").shouldHave(size(4));
    getElements(By.cssSelector("#radioButtons input")).shouldHave(size(4));

    $("#radioButtons").$$("input").shouldHave(size(4));
    $("#radioButtons").$$(By.tagName("input")).shouldHave(size(4));
    $("#radioButtons").findAll("input").shouldHave(size(4));
    $("#radioButtons").findAll(By.tagName("input")).shouldHave(size(4));
  }

  @Test
  void invalidSelector() {
    Assertions.assertThrows(InvalidSelectorException.class,
      () -> $$(By.xpath("//xxx[@'")).shouldHave(size(0)));
  }

  @Test
  void canUseSizeMethod() {
    Assertions.assertEquals(1, $$(By.name("domain")).size());
    Assertions.assertEquals(1, $$("#theHiddenElement").size());
    Assertions.assertEquals(4, $$("#radioButtons input").size());
    Assertions.assertEquals(4, $$(By.xpath("//select[@name='domain']/option")).size());
    Assertions.assertEquals(0, $$(By.name("non-existing-element")).size());
  }

  @Test
  void canCheckIfCollectionIsEmpty() {
    $$(By.name("#dynamic-content-container span")).shouldBe(empty);
    $$(By.name("non-existing-element")).shouldBe(empty);
    $$(byText("Loading...")).shouldBe(empty);
  }

  @Test
  void canCheckSizeOfCollection() {
    $$(By.name("domain")).shouldHaveSize(1);
    $$("#theHiddenElement").shouldHaveSize(1);
    $$("#radioButtons input").shouldHaveSize(4);
    $$(By.xpath("//select[@name='domain']/option")).shouldHaveSize(4);
    $$(By.name("non-existing-element")).shouldHaveSize(0);
    $$("#dynamic-content-container span").shouldHave(size(2));
  }

  @Test
  void shouldWaitUntilCollectionGetsExpectedSize() {
    ElementsCollection spans = $$("#dynamic-content-container span");

    spans.shouldHave(size(2)); // appears after 2 seconds

    Assertions.assertEquals(2, spans.size());
    Assertions.assertArrayEquals(new String[]{"dynamic content", "dynamic content2"}, spans.getTexts());
  }

  @Test
  void canCheckThatElementsHaveCorrectTexts() {
    $$("#dynamic-content-container span").shouldHave(
      texts("dynamic content", "dynamic content2"),
      texts("mic cont", "content2"),
      exactTexts(asList("dynamic content", "dynamic content2")));
  }

  @Test
  void ignoresWhitespacesInTexts() {
    $$("#dynamic-content-container span").shouldHave(
      texts("   dynamic \ncontent ", "dynamic \t\t\tcontent2\t\t\r\n"),
      exactTexts("dynamic \t\n content\n\r", "    dynamic content2      "));
  }

  @Test
  void canCheckThatElementsHaveExactlyCorrectTexts() {
    Assertions.assertThrows(TextsMismatch.class,
      () -> $$("#dynamic-content-container span").shouldHave(exactTexts("content", "content2")));
  }

  @Test
  void textsCheckThrowsElementNotFound() {
    Assertions.assertThrows(ElementNotFound.class,
      () -> $$(".non-existing-elements").shouldHave(texts("content1", "content2")));
  }

  @Test
  void exactTextsCheckThrowsElementNotFound() {
    Assertions.assertThrows(ElementNotFound.class,
      () -> $$(".non-existing-elements").shouldHave(exactTexts("content1", "content2")));
  }

  @Test
  void textsCheckThrowsTextsMismatch() {
    Assertions.assertThrows(TextsMismatch.class,
      () -> $$("#dynamic-content-container span").shouldHave(texts("static-content1", "static-content2", "static3")));
  }

  @Test
  void failsFast_ifNoExpectedTextsAreGiven() {
    Assertions.assertThrows(IllegalArgumentException.class,
      () -> $$("#dynamic-content-container span").shouldHave(texts()));
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
    Assertions.assertThrows(ElementNotFound.class,
      () -> $$("#multirowTable").findBy(text("INVALID-TEXT")).findAll("valid-selector").shouldHave(texts("foo bar")),
      "Element not found {#multirowTable.findBy(text 'INVALID-TEXT')}");
  }

  @Test
  void userCanFindMatchingElementFromList() {
    $$("#multirowTable tr").findBy(text("Norris")).shouldHave(text("Norris"));
  }

  @Test
  void findWaitsUntilElementMatches() {
    $$("#dynamic-content-container span").findBy(text("dynamic content2")).shouldBe(visible);
    $$("#dynamic-content-container span").findBy(text("unexisting")).shouldNot(exist);
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
    Assertions.assertTrue(it.hasNext());
    it.next().shouldHave(text("@livemail.ru"));

    Assertions.assertTrue(it.hasNext());
    it.next().shouldHave(text("@myrambler.ru"));

    Assertions.assertTrue(it.hasNext());
    it.next().shouldHave(text("@rusmail.ru"));

    Assertions.assertTrue(it.hasNext());
    it.next().shouldHave(text("@мыло.ру"));

    Assertions.assertFalse(it.hasNext());
  }

  @Test
  void canIterateCollection_withListIterator() {
    ListIterator<SelenideElement> it = $$("[name=domain] option").listIterator(3);
    Assertions.assertTrue(it.hasNext());
    Assertions.assertTrue(it.hasPrevious());
    it.previous().shouldHave(text("@rusmail.ru"));

    Assertions.assertTrue(it.hasPrevious());
    it.previous().shouldHave(text("@myrambler.ru"));

    Assertions.assertTrue(it.hasPrevious());
    it.previous().shouldHave(text("@livemail.ru"));

    Assertions.assertFalse(it.hasPrevious());

    it.next().shouldHave(text("@livemail.ru"));
    Assertions.assertTrue(it.hasPrevious());
  }

  @Test
  void canGetFirstNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option");
    collection.first(2).shouldHaveSize(2);
    collection.first(10).shouldHaveSize(collection.size());

    List<String> regularSublist = $$x("//select[@name='domain']/option").stream()
      .map(SelenideElement::getText)
      .collect(Collectors.toList()).subList(0, 2);

    List<String> selenideSublist = collection.first(2).stream()
      .map(SelenideElement::getText)
      .collect(Collectors.toList());

    Assertions.assertEquals(regularSublist, selenideSublist);
  }

  @Test
  void canGetLastNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option");
    collection.last(2).shouldHaveSize(2);
    collection.last(10).shouldHaveSize(collection.size());

    List<String> regularSublist = $$x("//select[@name='domain']/option").stream()
      .map(SelenideElement::getText)
      .collect(Collectors.toList()).subList(2, collection.size());

    List<String> selenideSublist = collection.last(2).stream()
      .map(SelenideElement::getText)
      .collect(Collectors.toList());

    Assertions.assertEquals(regularSublist, selenideSublist);
  }

  @Test
  void canChainFilterAndFirst() {
    $$("div").filterBy(visible).first()
      .shouldBe(visible)
      .shouldHave(text("non-clickable element"));

    $$("div").filterBy(visible).get(2).click();
  }
}
