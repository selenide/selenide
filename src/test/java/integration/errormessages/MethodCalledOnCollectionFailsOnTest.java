package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.ex.UIAssertionError;
import integration.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static integration.errormessages.Helper.assertScreenshot;
import static integration.helpers.HTMLBuilderForTestPreconditions.Given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class MethodCalledOnCollectionFailsOnTest extends IntegrationTest {

  @Before
  public void openPage() {
    Given.openedPageWithBody(
        "<ul>Hello to:",
        "<li class='the-expanse detective'>Miller</li>",
        "<li class='the-expanse missing'>Julie Mao</li>",
        "</ul>"
    );
    Configuration.timeout = 0;
  }

  @Test
  public void shouldCondition_When$$Collection_WithNonExistentWebElements() {
    ElementsCollection collection = $$("ul .nonexistent");

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul .nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: [Miller, Julie Mao]"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), nullValue());
    }
        /*
            Element not found {ul .nonexistent}
            Expected: [Miller, Julie Mao]

            Screenshot: file:/..._WithNonExistentWebElements/1471354880814.0.png
            Timeout: 6 s.
        */
        /*
            todo -
            We were looking for "collection of elements" but error message tells us about one "Element not found" o_O
            this also applies to all other "collection" based tests.
            Expected clause sounds too weird, because the context is not obvious, it would be better
             if such contexts preceded it:
                While waiting for condition: exactTexts
                Expected: [Miller, Julie Mao]
        */
  }


  @Test
  public void shouldCondition_WhenFilteredCollection_On$$CollectionWithNonExistentWebElements() {
    ElementsCollection collection = $$("ul .nonexistent").filter(cssClass("the-expanse"));

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul .nonexistent.filter(css class 'the-expanse')}"));
      assertThat(expected.getMessage(), containsString("Expected: [Miller, Julie Mao]"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), nullValue());
    }
        /*
            Element not found {ul .nonexistent.filter(css class 'the-expanse')}
            Expected: [Miller, Julie Mao]

            Screenshot: file:/..._WithNonExistentCollection/1471391641817.0.png
            Timeout: 6 s.
        */
  }

  @Test
  public void shouldCondition_WhenFilteredCollection_WithNotSatisfiedCondition() {
    ElementsCollection collection = $$("ul li").filter(cssClass("nonexistent"));

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul li.filter(css class 'nonexistent')}"));
      assertThat(expected.getMessage(), containsString("Expected: [Miller, Julie Mao]"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), nullValue());
    }
        /*
            Element not found {ul li.filter(css class 'nonexistent')}
            Expected: [Miller, Julie Mao]

            Screenshot: file:/..._WithNotSatisfiedCondition/1477042881706.0.png
            Timeout: 6 s.
        */
  }

  @Test
  public void shouldCondition_WhenInnerCollection_WithNonExistentOuterWebElement() {
    ElementsCollection collection = $(".nonexistent").findAll("li");

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {.nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: exist")); // todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(NoSuchElementException.class));
      assertCauseMessage(expected);
    }
        /*
            Element not found {.nonexistent}
            Expected: exist

            Screenshot: file:/..._WithNonExistentOuterWebElement/1471818981483.1.png
            Timeout: 6 s.
            Caused by: 
            NoSuchElementException: Unable to locate element: {"method":"css selector","selector":".nonexistent"}
        */
  }

  @Test
  public void shouldCondition_WhenInnerCollection_WithNonExistentInnerWebElements() {
    ElementsCollection collection = $("ul").findAll(".nonexistent");

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {<ul>/.nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: [Miller, Julie Mao]"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), nullValue());
    }
        /*
            Element not found {<ul>/.nonexistent}
            Expected: [Miller, Julie Mao]

            Screenshot: file:/..._WithNonExistentInnerWebElements/1471819518459.0.png
            Timeout: 6 s.
        */
  }

  /*******************************************************************************************************************
   * todo - hypothesis - error should be according to condition plus caused by ElementNotFound
   * Question - what should the correct error be?
   * now we have different options - exactTexts - Element not found, size - ListSizeMismatch without caused error
   * <p/>
   * look at
   * shouldHaveSizeCondition_When$$Collection_WithNotSatisfiedConditionInShould() - correct exception according to condition
   * shouldHaveSizeCondition_When$$Collection_WithNonExistentCollection() - correct exception according to condition,
   * BUT there is no caused by  - ElementNotFound
   * BUT
   * shouldCondition_When$$Collection_WithNonExistentWebElements() (using exactText) - we have ElementNotFound exception
   * instead error according to condition
   * <p/>
   * What is a correct result?
   */
  @Test
  public void shouldHaveSizeCondition_When$$Collection_WithNotSatisfiedConditionInShould() {
    ElementsCollection collection = $$("ul li");

    try {
      collection.shouldHave(size(3));
      fail("Expected ElementNotFound");
    }
    catch (ListSizeMismatch expected) {
      assertThat(expected.getMessage(), startsWith(": expected: = 3, actual: 2, collection: ul li"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), nullValue());
    }
        /*
            ListSizeMismatch : expected: = 3, actual: 2, collection: ul li
            Elements: [
                <li class="the-expanse detective" value="0">Miller</li>,
                <li class="the-expanse" value="0">Julie Mao</li>
            ]

            Screenshot: file:/..._WithNotSatisfiedConditionInShould/1471356041663.0.png
            Timeout: 6 s.
        */
  }

  @Test
  public void shouldHaveSizeCondition_When$$Collection_WithNonExistentCollection() {
    ElementsCollection collection = $$("ul .nonexistent");

    try {
      collection.shouldHave(size(3));
      fail("Expected ElementNotFound");
    }
    catch (ListSizeMismatch expected) {
      assertThat(expected.getMessage(), startsWith(": expected: = 3, actual: 0, collection: ul .nonexistent"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), nullValue());
    }
        /*
            ListSizeMismatch : expected: = 3, actual: 0, collection: ul .nonexistent
            Elements: []

            Screenshot: file:/..._WithNonExistentCollection/1471357025434.0.png
            Timeout: 6 s.
        */
  }

  private void assertCauseMessage(UIAssertionError expected) {
    if (isHtmlUnit()) {
      assertThat(expected.getCause().getMessage(), containsString("Returned node was not a DOM element"));
    }
    else if (isPhantomjs()) {
      assertThat(expected.getCause().getMessage(), containsString("Unable to find element with css selector '.nonexistent'"));
    }
    else {
      assertThat(expected.getCause().getMessage(),
          containsString("Unable to locate element: {\"method\":\"css selector\",\"selector\":\".nonexistent\"}"));
    }
  }
}
