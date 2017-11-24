package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class CollectionMethodsTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void useTwoDollarsToGetListOfElements() {
    $$("#radioButtons input").shouldHave(size(4));
    getElements(By.cssSelector("#radioButtons input")).shouldHave(size(4));

    $("#radioButtons").$$("input").shouldHave(size(4));
    $("#radioButtons").$$(By.tagName("input")).shouldHave(size(4));
    $("#radioButtons").findAll("input").shouldHave(size(4));
    $("#radioButtons").findAll(By.tagName("input")).shouldHave(size(4));
  }

  @Test(expected = InvalidSelectorException.class)
  public void invalidSelector() {
    $$(By.xpath("//xxx[@'")).shouldHave(size(0));
  }

  @Test
  public void canUseSizeMethod() {
    assertEquals(1, $$(By.name("domain")).size());
    assertEquals(1, $$("#theHiddenElement").size());
    assertEquals(4, $$("#radioButtons input").size());
    assertEquals(4, $$(By.xpath("//select[@name='domain']/option")).size());
    assertEquals(0, $$(By.name("non-existing-element")).size());
  }

  @Test
  public void canCheckIfCollectionIsEmpty() {
    $$(By.name("#dynamic-content-container span")).shouldBe(empty);
    $$(By.name("non-existing-element")).shouldBe(empty);
    $$(byText("Loading...")).shouldBe(empty);
  }

  @Test
  public void canCheckSizeOfCollection() {
    $$(By.name("domain")).shouldHaveSize(1);
    $$("#theHiddenElement").shouldHaveSize(1);
    $$("#radioButtons input").shouldHaveSize(4);
    $$(By.xpath("//select[@name='domain']/option")).shouldHaveSize(4);
    $$(By.name("non-existing-element")).shouldHaveSize(0);
    $$("#dynamic-content-container span").shouldHave(size(2));
  }

  @Test
  public void shouldWaitUntilCollectionGetsExpectedSize() {
    ElementsCollection spans = $$("#dynamic-content-container span");

    spans.shouldHave(size(2)); // appears after 2 seconds

    assertEquals(2, spans.size());
    assertArrayEquals(new String[]{"dynamic content", "dynamic content2"}, spans.getTexts());
  }

  @Test
  public void canCheckThatElementsHaveCorrectTexts() {
    $$("#dynamic-content-container span").shouldHave(
        texts("dynamic content", "dynamic content2"),
        texts("mic cont", "content2"),
        exactTexts(asList("dynamic content", "dynamic content2")));
  }

  @Test
  public void ignoresWhitespacesInTexts() {
    $$("#dynamic-content-container span").shouldHave(
        texts("   dynamic \ncontent ", "dynamic \t\t\tcontent2\t\t\r\n"),
        exactTexts("dynamic \t\n content\n\r", "    dynamic content2      "));
  }

  @Test(expected = TextsMismatch.class)
  public void canCheckThatElementsHaveExactlyCorrectTexts() {
    $$("#dynamic-content-container span").shouldHave(
        exactTexts("content", "content2"));
  }

  @Test(expected = ElementNotFound.class)
  public void textsCheckThrowsElementNotFound() {
    $$(".non-existing-elements").shouldHave(texts("content1", "content2"));
  }

  @Test(expected = ElementNotFound.class)
  public void exactTextsCheckThrowsElementNotFound() {
    $$(".non-existing-elements").shouldHave(exactTexts("content1", "content2"));
  }

  @Test(expected = TextsMismatch.class)
  public void textsCheckThrowsTextsMismatch() {
    $$("#dynamic-content-container span").shouldHave(texts("static-content1", "static-content2", "static3"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void failsFast_ifNoExpectedTextsAreGiven() {
    $$("#dynamic-content-container span").shouldHave(texts());
  }

  @Test
  public void userCanFilterOutMatchingElements() {
    $$("#multirowTable tr").shouldHaveSize(2);
    $$("#multirowTable tr").filterBy(text("Norris")).shouldHaveSize(1);
    $$("#multirowTable tr").filterBy(cssClass("inexisting")).shouldHaveSize(0);
  }

  @Test
  public void userCanFilterElementsByConditionOnChild() {
    ElementsCollection collectionToFilter = $$("#multirowTable tr");
    ElementsCollection collectionFiltered = collectionToFilter.filterBy(child("td:nth-child(2)", text("Norris")));
    collectionToFilter.shouldHaveSize(2);
    collectionFiltered.shouldHaveSize(1);
  }

  @Test
  public void userCanExcludeMatchingElements() {
    $$("#multirowTable tr").shouldHaveSize(2);
    $$("#multirowTable tr").excludeWith(text("Chack")).shouldHaveSize(0);
    $$("#multirowTable tr").excludeWith(cssClass("inexisting")).shouldHaveSize(2);
  }

  @Test
  public void errorMessageShouldShow_whichElementInChainWasNotFound() {
    thrown.expect(ElementNotFound.class);
    thrown.expectMessage("Element not found {#multirowTable.findBy(text 'INVALID-TEXT')}");
    
    $$("#multirowTable").findBy(text("INVALID-TEXT")).findAll("valid-selector").shouldHave(texts("foo bar"));
  }

  @Test
  public void userCanFindMatchingElementFromList() {
    $$("#multirowTable tr").findBy(text("Norris")).shouldHave(text("Norris"));
  }

  @Test
  public void findWaitsUntilElementMatches() {
    $$("#dynamic-content-container span").findBy(text("dynamic content2")).shouldBe(visible);
    $$("#dynamic-content-container span").findBy(text("unexisting")).shouldNot(exist);
  }

  @Test
  public void collectionMethodsCanBeChained() {
    $$("#multirowTable tr").shouldHave(size(2))
        .filterBy(text("Norris")).shouldHave(size(1));
  }

  @Test
  public void shouldMethodsCanCheckMultipleConditions() {
    $$("#multirowTable tr td").shouldHave(size(4), texts(asList("Chack", "Norris", "Chack", "L'a Baskerville")));
  }

  @Test
  public void canGetCollectionElementByIndex() {
    $$("#radioButtons input").get(0).shouldHave(value("master"));
    $$("#radioButtons input").get(1).shouldHave(value("margarita"));
    $$("#radioButtons input").get(2).shouldHave(value("cat"));
    $$("#radioButtons input").get(3).shouldHave(value("woland"));
  }

  @Test
  public void canGetCollectionFirstElement() {
    $$("#radioButtons input").first().shouldHave(value("master"));
  }

  @Test
  public void canGetCollectionLastElement() {
    $$("#radioButtons input").last().shouldHave(value("woland"));
  }
  
  @Test
  public void canFindElementsByMultipleSelectors() {
    $$(".first_row").shouldHave(size(1));
    $$(".second_row").shouldHave(size(1));
    $$(".first_row,.second_row").shouldHave(size(2));
  }

  @Test
  public void canIterateCollection_withIterator() {
    Iterator<SelenideElement> it = $$("[name=domain] option").iterator();
    assertTrue(it.hasNext()); 
    it.next().shouldHave(text("@livemail.ru"));

    assertTrue(it.hasNext()); 
    it.next().shouldHave(text("@myrambler.ru"));
    
    assertTrue(it.hasNext()); 
    it.next().shouldHave(text("@rusmail.ru"));
    
    assertTrue(it.hasNext()); 
    it.next().shouldHave(text("@мыло.ру"));
  
    assertFalse(it.hasNext());
  }

  @Test
  public void canIterateCollection_withListIterator() {
    ListIterator<SelenideElement> it = $$("[name=domain] option").listIterator(3);
    assertTrue(it.hasNext());
    assertTrue(it.hasPrevious());
    it.previous().shouldHave(text("@rusmail.ru"));

    assertTrue(it.hasPrevious());
    it.previous().shouldHave(text("@myrambler.ru"));

    assertTrue(it.hasPrevious());
    it.previous().shouldHave(text("@livemail.ru"));

    assertFalse(it.hasPrevious());

    it.next().shouldHave(text("@livemail.ru"));
    assertTrue(it.hasPrevious());
  }

  @Test
  public void canGetFirstNElements() {
    ElementsCollection collection =  $$x("//select[@name='domain']/option");
    collection.first(2).shouldHaveSize(2);
    collection.first(10).shouldHaveSize(collection.size());

    List<String> regularSublist = $$x("//select[@name='domain']/option").stream()
            .map(SelenideElement::getText)
            .collect(Collectors.toList()).subList(0, 2);

    List<String> selenideSublist = collection.first(2).stream()
            .map(SelenideElement::getText)
            .collect(Collectors.toList());

    Assert.assertEquals(regularSublist, selenideSublist);
  }

  @Test
  public void canGetLastNElements() {
    ElementsCollection collection =  $$x("//select[@name='domain']/option");
    collection.last(2).shouldHaveSize(2);
    collection.last(10).shouldHaveSize(collection.size());

    List<String> regularSublist = $$x("//select[@name='domain']/option").stream()
            .map(SelenideElement::getText)
            .collect(Collectors.toList()).subList(2, collection.size());

    List<String> selenideSublist = collection.last(2).stream()
            .map(SelenideElement::getText)
            .collect(Collectors.toList());

    Assert.assertEquals(regularSublist, selenideSublist);
  }
}
