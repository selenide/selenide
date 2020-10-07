package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.UIAssertionError;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static integration.errormessages.Helper.assertScreenshot;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

final class MethodCalledOnElementFailsOnTest extends IntegrationTest {
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
  void shouldCondition_When$Element_WithNonExistentElement() {
    SelenideElement element = $("ul .nonexistent");

    try {
      element.shouldHave(text("Miller"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul .nonexistent}");
      assertThat(expected)
        .hasMessageContaining("Expected: text 'Miller'");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(NoSuchElementException.class);
      assertCauseMessage(expected, "ul .nonexistent");
    }
  }

  private void assertCauseMessage(UIAssertionError expected, String selector) {
    if (isFirefox()) {
      assertThat(expected.getCause()).hasMessageContaining("Unable to locate element: " + selector);
    }
    else {
      assertThat(expected.getCause())
        .hasMessageContaining("Unable to locate element: {\"method\":\"css selector\",\"selector\":\"" + selector + "\"}");
    }
  }

  @Test
  void shouldCondition_WhenCollectionElementByIndex_WithNonExistentCollection() {
    SelenideElement element = $$("ul .nonexistent").get(1);

    try {
      element.shouldHave(text("Miller"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul .nonexistent[1]}");
      assertThat(expected)
        .hasMessageContaining("Expected: visible");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(IndexOutOfBoundsException.class);
      assertThat(expected.getCause())
        .hasMessageMatching("Index\\D+1\\D+0");
    }
  }

  @Test
  void shouldCondition_WhenCollectionElementByIndex_WithIndexOutOfRange() {
    SelenideElement element = $$("ul li").get(10);

    try {
      element.shouldHave(text("Miller"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul li[10]}");
      assertThat(expected)
        .hasMessageContaining("Expected: text 'Miller'");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(IndexOutOfBoundsException.class);
      assertThat(expected.getCause())
        .hasMessageMatching("Index\\D+10\\D+2");
    }
  }

  @Test
  void actionWithVisibilityWaiting_WhenCollectionElementByIndex_WithIndexOutOfRange() {
    SelenideElement element = $$("ul li").get(10);

    try {
      element.click();
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul li[10]}");
      assertThat(expected)
        .hasMessageContaining("Expected: visible");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(IndexOutOfBoundsException.class);
      assertThat(expected.getCause())
        .hasMessageMatching("Index\\D+10\\D+2");
    }
  }

  @Test
  void shouldCondition_WhenCollectionElementByCondition_WithNonExistentCollection() {
    SelenideElement element = $$(".nonexistent").findBy(cssClass("the-expanse"));

    try {
      element.shouldBe(exist);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {.nonexistent.findBy(css class 'the-expanse')}");
      assertThat(expected)
        .hasMessageContaining("Expected: exist");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(ElementNotFound.class);
      assertThat(expected.getCause())
        .hasMessageStartingWith("Element not found {.nonexistent.findBy(css class 'the-expanse')}");
    }
  }

  @Test
  void shouldCondition_WhenCollectionElementByCondition_WithNotSatisfiedCondition() {
    SelenideElement element = $$("li").findBy(cssClass("nonexistent"));

    try {
      element.shouldBe(visible);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {li.findBy(css class 'nonexistent')}");
      assertThat(expected)
        .hasMessageContaining("Expected: visible");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(ElementNotFound.class);
      assertThat(expected.getCause())
        .hasMessageStartingWith("Element not found {li.findBy(css class 'nonexistent')}");
    }
  }

  @Test
  void actionWithExistenceWaiting_WhenCollectionElementByCondition_WithNotSatisfiedCondition() {
    SelenideElement element = $$("li").findBy(cssClass("nonexistent"));

    try {
      element.text();
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {li.findBy(css class 'nonexistent')}");
      assertThat(expected)
        .hasMessageContaining("Expected: css class 'nonexistent'"); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isNull();
    }
  }

  @Test
  void shouldCondition_WhenInnerElement_WithNonExistentOuterElement() {
    SelenideElement element = $(".nonexistent").find(".the-expanse");

    try {
      element.should(appear);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {.nonexistent}");
      assertThat(expected)
        .hasMessageContaining("Expected: exist"); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(NoSuchElementException.class);
      assertCauseMessage(expected, ".nonexistent");
    }
  }

  @Test
  void shouldCondition_WhenInnerElement_WithNonExistentInnerElement() {
    SelenideElement element = $("ul").find(".nonexistent");

    try {
      element.shouldBe(visible);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {.nonexistent}");
      assertThat(expected)
        .hasMessageContaining("Expected: visible");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(NoSuchElementException.class);
      assertCauseMessage(expected, ".nonexistent");
    }
  }

  @Test
  void actionWithVisibilityWaiting_WhenInnerElement_WithNonExistentInnerElement() {
    SelenideElement element = $("ul").find(".nonexistent");

    try {
      element.doubleClick();
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {.nonexistent}");
      assertThat(expected)
        .hasMessageContaining("Expected: visible");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(NoSuchElementException.class);
      assertCauseMessage(expected, ".nonexistent");
    }
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithNonExistentStartCollection() {
    SelenideElement element = $$("ul .nonexistent").filterBy(cssClass("the-expanse")).findBy(cssClass("detective")).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul .nonexistent.filter(css class 'the-expanse').findBy(css class 'detective')}");
      assertThat(expected)
        .hasMessageContaining("Expected: exist");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(ElementNotFound.class);
      assertThat(expected.getCause())
        .hasMessageStartingWith("Element not found {ul .nonexistent.filter(css class 'the-expanse').findBy(css class 'detective')}");
    }
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithEmptyFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("nonexistent")).findBy(cssClass("detective")).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul li.filter(css class 'nonexistent').findBy(css class 'detective')}");
      assertThat(expected)
        .hasMessageContaining("Expected: exist");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(ElementNotFound.class);
      assertThat(expected.getCause())
        .hasMessageStartingWith("Element not found {ul li.filter(css class 'nonexistent').findBy(css class 'detective')}");
    }
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithNonExistentOuterElement() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).findBy(cssClass("nonexistent")).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul li.filter(css class 'the-expanse').findBy(css class 'nonexistent')}");
      assertThat(expected)
        .hasMessageContaining("Expected: exist"); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(ElementNotFound.class);
      assertThat(expected.getCause())
        .hasMessageStartingWith("Element not found {ul li.filter(css class 'the-expanse').findBy(css class 'nonexistent')}");
    }
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithNonExistentInnerElement() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).findBy(cssClass("detective")).find(".nonexistent");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {.nonexistent}");
      assertThat(expected)
        .hasMessageContaining("Expected: exact text 'detective'");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(NoSuchElementException.class);
      assertCauseMessage(expected, ".nonexistent");
    }
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithNonExistentStartCollection() {
    SelenideElement element = $$("ul .nonexistent").filterBy(cssClass("the-expanse")).get(0).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul .nonexistent.filter(css class 'the-expanse')[0]}");
      assertThat(expected)
        .hasMessageContaining("Expected: visible");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(IndexOutOfBoundsException.class);
      assertThat(expected.getCause())
        .hasMessageMatching("Index: 0");
    }
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithEmptyFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("nonexistent")).get(0).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul li.filter(css class 'nonexistent')[0]}");
      assertThat(expected)
        .hasMessageContaining("Expected: visible");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(IndexOutOfBoundsException.class);
      assertThat(expected.getCause())
        .hasMessageMatching("Index: 0");
    }
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithIndexOutOfRange() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).get(2).find("label");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {ul li.filter(css class 'the-expanse')[2]}");
      assertThat(expected)
        .hasMessageContaining("Expected: exist"); //todo - is it correct?
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(IndexOutOfBoundsException.class);
      assertThat(expected.getCause())
        .hasMessageMatching("Index: 2");
    }
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithNonExistentInnerElement() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).get(0).find(".nonexistent");

    try {
      element.shouldHave(exactText("detective"));
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expected) {
      assertThat(expected)
        .hasMessageStartingWith("Element not found {.nonexistent}");
      assertThat(expected)
        .hasMessageContaining("Expected: exact text 'detective'");
      assertScreenshot(expected);
      assertThat(expected.getCause())
        .isInstanceOf(NoSuchElementException.class);
      assertCauseMessage(expected, ".nonexistent");
    }
  }
}
