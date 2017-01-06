package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.UIAssertionError;
import integration.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static integration.errormessages.Helper.assertScreenshot;
import static integration.helpers.HTMLBuilderForTestPreconditions.Given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class MethodCalledOnElementFailsOnTest extends IntegrationTest {

  @Before
  public void openPage() {
    Given.openedPageWithBody(
        "<ul>Hello to:",
        "<li class='the-expanse detective'>Miller <label>detective</label></li>",
        "<li class='the-expanse missing'>Julie Mao</li>",
        "</ul>"
    );
    Configuration.timeout = 0;
  }

  @Test
  public void shouldCondition_When$Element_WithNonExistentElement() {
    SelenideElement element = $("ul .nonexistent");

    try {
      element.shouldHave(text("Miller"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul .nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: text 'Miller'"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(NoSuchElementException.class));
      assertCauseMessage(expected);
    }
        /*
            caused by - different expression for chrome & ff

            ff
            Element not found {ul .nonexistent}
            Expected: text 'Miller'

            Screenshot: file:/..._WithNonExistentWebElement/1471304286016.0.png
            Timeout: 0 ms.
            Caused by: 
            NoSuchElementException: Unable to locate element: {"method":"css selector","selector":"ul .nonexistent"}

            chrome
            Element not found {ul .nonexistent}
            Expected: text 'Miller'

            Screenshot: file:/..._WithNonExistentElement/1477036866229.0.png
            Timeout: 0 ms.
            Caused by: NoSuchElementException: no such element: 
              Unable to locate element: {"method":"css selector","selector":"ul .nonexistent"}

        */
  }

  @Test
  public void shouldCondition_WhenCollectionElementByIndex_WithNonExistentCollection() {
    SelenideElement element = $$("ul .nonexistent").get(1);

    try {
      element.shouldHave(text("Miller"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul .nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: visible")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(IndexOutOfBoundsException.class));
      assertThat(expected.getCause().getMessage(), startsWith("Index: 1, Size: 0"));
    }
    //todo - is it OK??
        /*
            Element not found {ul .nonexistent}
            Expected: visible

            Screenshot: file:/..._WithNonExistentCollection/1471345897750.0.png
            Timeout: 0 ms.
            Caused by: java.lang.IndexOutOfBoundsException: Index: 1, Size: 0
        */

  }

  @Test
  public void shouldCondition_WhenCollectionElementByIndex_WithIndexOutOfRange() {
    SelenideElement element = $$("ul li").get(10);

    try {
      element.shouldHave(text("Miller"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul li[10]}"));
      assertThat(expected.getMessage(), containsString("Expected: text 'Miller'"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(IndexOutOfBoundsException.class));
      assertThat(expected.getCause().getMessage(), startsWith("Index: 10, Size: 2"));
    }
        /*
            Element not found {ul li[10]}
            Expected: text 'Miller'

            Screenshot: file:/..._WithIndexOutOfRange/1471346375481.0.png
            Timeout: 0 ms.
            Caused by: java.lang.IndexOutOfBoundsException: Index: 10, Size: 2
        */
  }

  @Test
  public void actionWithVisibilityWaiting_WhenCollectionElementByIndex_WithIndexOutOfRange() {
    SelenideElement element = $$("ul li").get(10);

    try {
      element.click();
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul li[10]}"));
      assertThat(expected.getMessage(), containsString("Expected: visible"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(IndexOutOfBoundsException.class));
      assertThat(expected.getCause().getMessage(), startsWith("Index: 10, Size: 2"));
    }
        /*
            Element not found {ul li[10]}
            Expected: visible

            Screenshot: file:/..._WithIndexOutOfRange/1471347320304.0.png
            Timeout: 0 ms.
            Caused by: java.lang.IndexOutOfBoundsException: Index: 10, Size: 2
        */
  }

  @Test
  public void shouldCondition_WhenCollectionElementByCondition_WithNonExistentCollection() {
    SelenideElement element = $$(".nonexistent").findBy(cssClass("the-expanse"));

    try {
      element.shouldBe(exist);
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {.nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: visible")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(ElementNotFound.class));
      assertThat(expected.getCause().getMessage(), startsWith("Element not found {.nonexistent.findBy(css class 'the-expanse')}"));
    }
        /*
            Element not found {.nonexistent}
            Expected: visible

            Screenshot: file:/..._WithNonExistentCollection/1471347808745.0.png
            Timeout: 0 ms.
            Caused by: Element not found {.nonexistent.findBy(css class 'the-expanse')}
            Expected: css class 'the-expanse'
            Screenshot: null
            Timeout: 0 ms.
        */
  }

  @Test
  public void shouldCondition_WhenCollectionElementByCondition_WithNotSatisfiedCondition() {
    SelenideElement element = $$("li").findBy(cssClass("nonexistent"));

    try {
      element.shouldBe(visible);
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {li.findBy(css class 'nonexistent')}"));
      assertThat(expected.getMessage(), containsString("Expected: visible"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(ElementNotFound.class));
      assertThat(expected.getCause().getMessage(), startsWith("Element not found {li.findBy(css class 'nonexistent')}"));
    }
        /*
            Element not found {li.findBy(css class 'nonexistent')}
            Expected: visible

            Screenshot: file:/..._WithNotSatisfiedCondition/1471348458110.0.png
            Timeout: 0 ms.
            Caused by: Element not found {li.findBy(css class 'nonexistent')}
            Expected: css class 'nonexistent'
            Screenshot: null
            Timeout: 0 ms.
        */
  }

  @Test
  public void actionWithExistenceWaiting_WhenCollectionElementByCondition_WithNotSatisfiedCondition() {
    SelenideElement element = $$("li").findBy(cssClass("nonexistent"));

    try {
      element.text();
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {li.findBy(css class 'nonexistent')}"));
      assertThat(expected.getMessage(), containsString("Expected: css class 'nonexistent'")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), nullValue());
    }
        /*
            Element not found {li.findBy(css class 'nonexistent')}
            Expected: css class 'nonexistent'

            Screenshot: file:/..._WithNotSatisfiedCondition/1471348636944.0.png
            Timeout: 0 ms.
        */
  }

  @Test
  public void shouldCondition_WhenInnerElement_WithNonExistentOuterElement() {
    SelenideElement element = $(".nonexistent").find(".the-expanse");

    try {
      element.should(appear);
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {.nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: exist")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(NoSuchElementException.class));
      assertCauseMessage(expected);
    }
        /*
            Element not found {.nonexistent}
            Expected: exist

            Screenshot: file:/..._WithNonExistentOuterElement/1471351158103.1.png
            Timeout: 0 ms.
            Caused by: NoSuchElementException: Unable to locate element: {"method":"css selector","selector":".nonexistent"}
        */
  }

  @Test
  public void shouldCondition_WhenInnerElement_WithNonExistentInnerElement() {
    SelenideElement element = $("ul").find(".nonexistent");

    try {
      element.shouldBe(visible);
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {.nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: visible"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(NoSuchElementException.class));
      assertCauseMessage(expected);
    }
        /*
            Element not found {.nonexistent}
            Expected: visible

            Screenshot: file:/..._WithNonExistentInnerElement/1471352505699.0.png
            Timeout: 0 ms.
            Caused by: 
            NoSuchElementException: Unable to locate element: {"method":"css selector","selector":".nonexistent"}
        */
  }


  @Test
  public void actionWithVisibilityWaiting_WhenInnerElement_WithNonExistentInnerElement() {
    SelenideElement element = $("ul").find(".nonexistent");

    try {
      element.doubleClick();
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {.nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: visible"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(NoSuchElementException.class));
      assertCauseMessage(expected);
    }
        /*
            Element not found {.nonexistent}
            Expected: visible

            Screenshot: file:..._WithNonExistentInnerElement/1471353844670.0.png
            Timeout: 0 ms.
            Caused by: 
            NoSuchElementException: Unable to locate element: {"method":"css selector","selector":".nonexistent"}
        */
  }

  /******************************************************
   * More complicated useful options
   * $$.filterBy(condition).findBy(condition).find
   ******************************************************/

  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithNonExistentStartCollection() {
    SelenideElement element = $$("ul .nonexistent").filterBy(cssClass("the-expanse")).findBy(cssClass("detective")).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul .nonexistent.filter(css class 'the-expanse')}"));
      assertThat(expected.getMessage(), containsString("Expected: visible")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(ElementNotFound.class));
      assertThat(expected.getCause().getMessage(),
          startsWith("Element not found {ul .nonexistent.filter(css class 'the-expanse').findBy(css class 'detective')}"));
    }
        /*
            Element not found {ul .nonexistent.filter(css class 'the-expanse')}
            Expected: visible

            Screenshot: file:/..._WithNonExistentStartCollection/1471821878793.1.png
            Timeout: 0 ms.
            Caused by: Element not found {ul .nonexistent.filter(css class 'the-expanse').findBy(css class 'detective')}
            Expected: css class 'detective'
            Screenshot: null
            Timeout: 0 ms.
        */
  }

  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithEmptyFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("nonexistent")).findBy(cssClass("detective")).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul li.filter(css class 'nonexistent')}"));
      assertThat(expected.getMessage(), containsString("Expected: visible")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(ElementNotFound.class));
      assertThat(expected.getCause().getMessage(),
          startsWith("Element not found {ul li.filter(css class 'nonexistent').findBy(css class 'detective')}"));
    }
        /*
            Element not found {ul li.filter(css class 'nonexistent')}
            Expected: visible

            Screenshot: file:/..._WithEmptyFilteredCollection/1471822913839.1.png
            Timeout: 0 ms.
            Caused by: Element not found {ul li.filter(css class 'nonexistent').findBy(css class 'detective')}
            Expected: css class 'detective'
            Screenshot: null
            Timeout: 0 ms.
        */
  }

  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithNonExistentOuterElement() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).findBy(cssClass("nonexistent")).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), 
          startsWith("Element not found {ul li.filter(css class 'the-expanse').findBy(css class 'nonexistent')}"));
      assertThat(expected.getMessage(), containsString("Expected: exist")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(ElementNotFound.class));
      assertThat(expected.getCause().getMessage(),
          startsWith("Element not found {ul li.filter(css class 'the-expanse').findBy(css class 'nonexistent')}"));
    }
        /*
            Element not found {ul li.filter(css class 'the-expanse').findBy(css class 'nonexistent')}
            Expected: exist

            Screenshot: file:/..._WithNonExistentOuterElement/1471823230953.1.png
            Timeout: 0 ms.
            Caused by: Element not found {ul li.filter(css class 'the-expanse').findBy(css class 'nonexistent')}
            Expected: css class 'nonexistent'
            Screenshot: null
            Timeout: 0 ms.
        */
  }

  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithNonExistentInnerElement() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).findBy(cssClass("detective")).find(".nonexistent");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {.nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: exact text 'detective'"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(NoSuchElementException.class));
      assertCauseMessage(expected);
    }
        /*
            Element not found {.nonexistent}
            Expected: exact text 'detective'

            Screenshot: file:/..._WithNonExistentInnerElement/1471823617503.0.png
            Timeout: 0 ms.
            Caused by: 
            NoSuchElementException: Unable to locate element: {"method":"css selector","selector":".nonexistent"}
        */
  }

  /******************************************************
   * More complicated useful options
   * $$.filterBy(condition).get(index).find
   ******************************************************/
  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithNonExistentStartCollection() {
    SelenideElement element = $$("ul .nonexistent").filterBy(cssClass("the-expanse")).get(0).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul .nonexistent.filter(css class 'the-expanse')}"));
      assertThat(expected.getMessage(), containsString("Expected: visible")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(IndexOutOfBoundsException.class));
      assertThat(expected.getCause().getMessage(), startsWith("Index: 0, Size: 0"));
    }
        /*
            Element not found {ul .nonexistent.filter(css class 'the-expanse')}
            Expected: visible

            Screenshot: file:/..._WithNonExistentStartCollection/1471824227187.1.png
            Timeout: 0 ms.
            Caused by: java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        */
  }

  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithEmptyFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("nonexistent")).get(0).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul li.filter(css class 'nonexistent')}"));
      assertThat(expected.getMessage(), containsString("Expected: visible")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(IndexOutOfBoundsException.class));
      assertThat(expected.getCause().getMessage(), startsWith("Index: 0, Size: 0"));
    }
        /*
            Element not found {ul li.filter(css class 'nonexistent')}
            Expected: visible

            Screenshot: file:/..._WithEmptyFilteredCollection/1471824645294.1.png
            Timeout: 0 ms.
            Caused by: java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        */
  }

  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithIndexOutOfRange() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).get(2).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {ul li.filter(css class 'the-expanse')[2]}"));
      assertThat(expected.getMessage(), containsString("Expected: exist")); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(IndexOutOfBoundsException.class));
      assertThat(expected.getCause().getMessage(), startsWith("Index: 2, Size: 2"));
    }
        /*
            Element not found {ul li.filter(css class 'the-expanse')[2]}
            Expected: exist

            Screenshot: file:/..._WithIndexOutOfRange/1471824926318.1.png
            Timeout: 0 ms.
            Caused by: java.lang.IndexOutOfBoundsException: Index: 2, Size: 2

        */
  }

  @Test
  public void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithNonExistentInnerElement() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).get(0).find(".nonexistent");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertThat(expected.getMessage(), startsWith("Element not found {.nonexistent}"));
      assertThat(expected.getMessage(), containsString("Expected: exact text 'detective'"));
      assertScreenshot(expected);
      assertThat(expected.getCause(), instanceOf(NoSuchElementException.class));
      assertCauseMessage(expected);
    }
        /*
            Element not found {.nonexistent}
            Expected: exact text 'detective'

            Screenshot: file:/..._WithNonExistentInnerElement/1471825188045.0.png
            Timeout: 0 ms.
            Caused by: 
            NoSuchElementException: Unable to locate element: {"method":"css selector","selector":".nonexistent"}
        */
  }

  private void assertCauseMessage(UIAssertionError expected) {
    if (!WebDriverRunner.isHtmlUnit()) {
      assertThat(expected.getCause().getMessage(),
          containsString("Unable to locate element: {\"method\":\"css selector\",\"selector\":\".nonexistent\"}"));
    }
  }
}
