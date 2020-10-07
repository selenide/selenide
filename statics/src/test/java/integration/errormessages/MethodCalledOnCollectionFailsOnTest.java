package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.ex.UIAssertionError;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static integration.errormessages.Helper.assertScreenshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

final class MethodCalledOnCollectionFailsOnTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    givenHtml(
      "<ul>Hello to:",
      "<li class='the-expanse detective'>Miller</li>",
      "<li class='the-expanse missing'>Julie Mao</li>",
      "</ul>"
    );
    Configuration.timeout = 1;
  }

  @Test
  void shouldCondition_When$$Collection_WithNonExistentWebElements() {
    ElementsCollection collection = $$("ul .nonexistent");

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul .nonexistent}");
      assertThat(expected)
        .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isNull();
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
  void shouldCondition_WhenFilteredCollection_On$$CollectionWithNonExistentWebElements() {
    ElementsCollection collection = $$("ul .nonexistent").filter(cssClass("the-expanse"));

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul .nonexistent.filter(css class 'the-expanse')}");
      assertThat(expected)
        .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isNull();
    }
        /*
            Element not found {ul .nonexistent.filter(css class 'the-expanse')}
            Expected: [Miller, Julie Mao]

            Screenshot: file:/..._WithNonExistentCollection/1471391641817.0.png
            Timeout: 6 s.
        */
  }

  @Test
  void shouldCondition_WhenFilteredCollection_WithNotSatisfiedCondition() {
    ElementsCollection collection = $$("ul li").filter(cssClass("nonexistent"));

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul li.filter(css class 'nonexistent')}");
      assertThat(expected)
        .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isNull();
    }
        /*
            Element not found {ul li.filter(css class 'nonexistent')}
            Expected: [Miller, Julie Mao]

            Screenshot: file:/..._WithNotSatisfiedCondition/1477042881706.0.png
            Timeout: 6 s.
        */
  }

  @Test
  void shouldCondition_WhenInnerCollection_WithNonExistentOuterWebElement() {
    ElementsCollection collection = $(".nonexistent").findAll("li");

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {.nonexistent/li}");
      assertThat(expected)
        .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(NoSuchElementException.class);
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

  private void assertCauseMessage(UIAssertionError expected) {
    String expectedCauseMessage = isFirefox()
      ? "Unable to locate element: .nonexistent"
      : "Unable to locate element: {\"method\":\"css selector\",\"selector\":\".nonexistent\"}";
    assertThat(expected.getCause())
      .hasMessageContaining(expectedCauseMessage);
  }

  @Test
  void shouldCondition_WhenInnerCollection_WithNonExistentInnerWebElements() {
    ElementsCollection collection = $("ul").findAll(".nonexistent");

    try {
      collection.shouldHave(exactTexts("Miller", "Julie Mao"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul/.nonexistent}");
      assertThat(expected)
        .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isNull();
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
   * <br>
   * look at
   * shouldHaveSizeCondition_When$$Collection_WithNotSatisfiedConditionInShould() - correct exception according to condition
   * shouldHaveSizeCondition_When$$Collection_WithNonExistentCollection() - correct exception according to condition,
   * BUT there is no caused by  - ElementNotFound
   * BUT
   * shouldCondition_When$$Collection_WithNonExistentWebElements() (using exactText) - we have ElementNotFound exception
   * instead error according to condition
   * <br>
   * What is a correct result?
   */
  @Test
  void shouldHaveSizeCondition_When$$Collection_WithNotSatisfiedConditionInShould() {
    ElementsCollection collection = $$("ul li");

    try {
      collection.shouldHave(size(3));
      fail("Expected ListSizeMismatch");
    } catch (ListSizeMismatch expected) {
      assertThat(expected).hasMessageStartingWith("List size mismatch: expected: = 3, actual: 2, collection: ul li");
      assertScreenshot(expected);
      assertThat(expected.getCause()).isNull();
    }
  }

  @Test
  void shouldHaveSizeCondition_When$$Collection_WithNonExistentCollection() {
    ElementsCollection collection = $$("ul .nonexistent");

    try {
      collection.shouldHave(size(3));
      fail("Expected ListSizeMismatch");
    } catch (ListSizeMismatch expected) {
      assertThat(expected).hasMessageStartingWith("List size mismatch: expected: = 3, actual: 0, collection: ul .nonexistent");
      assertScreenshot(expected);
      assertThat(expected.getCause()).isNull();
    }
  }
}
