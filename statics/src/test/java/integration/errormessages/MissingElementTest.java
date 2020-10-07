package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.UIAssertionError;
import integration.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.element;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class MissingElementTest extends IntegrationTest {
  private PageObject pageObject;
  private String reportsUrl;
  private String reportsFolder;

  @BeforeEach
  void setTimeout() {
    timeout = 15;
    pageObject = openFile("page_with_selects_without_jquery.html", PageObject.class);
  }

  @BeforeEach
  void mockScreenshots() {
    reportsUrl = Configuration.reportsUrl;
    reportsFolder = Configuration.reportsFolder;
    Configuration.reportsUrl = "http://ci.org/";
    Configuration.reportsFolder = "build/reports/tests";
  }

  @AfterEach
  void restoreScreenshots() {
    Configuration.reportsUrl = reportsUrl;
    Configuration.reportsFolder = reportsFolder;
  }

  @Test
  void elementNotFound() {
    try {
      $("h9").shouldHave(text("expected text"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/elementNotFound";
      assertThat(expected)
        .hasMessageMatching(String.format("Element not found \\{h9}%n" +
          "Expected: text 'expected text'%n" +
          "Screenshot: " + path + png() + "%n" +
          "Page source: " + path + html() + "%n" +
          "Timeout: 15 ms.%n" +
          "Caused by: NoSuchElementException:.*"));
      assertThat(expected.getScreenshot()).matches(path + pngOrHtml());
    }
  }

  @Test
  void elementTextDoesNotMatch() {
    String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/elementTextDoesNotMatch";
    assertThatThrownBy(() ->
      $("h2").shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text 'expected text' \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void elementAttributeDoesNotMatch() {
    String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/elementAttributeDoesNotMatch";
    assertThatThrownBy(() ->
      $("h2").shouldHave(attribute("name", "header"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have attribute name=\"header\" \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: name=\"\"%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void wrapperTextDoesNotMatch() {
    String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/wrapperTextDoesNotMatch";
    assertThatThrownBy(() ->
      $(element(By.tagName("h2"))).shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text 'expected text' \\{By.tagName: h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void clickHiddenElement() {
    String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/clickHiddenElement";
    assertThatThrownBy(() ->
      $("#theHiddenElement").click()
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format(
        "Element should be visible or transparent: visible or have css value opacity=0 \\{#theHiddenElement}%n" +
          "Element: '<div id=\"theHiddenElement\" displayed:false></div>'%n" +
          "Actual value: visible:false, 1%n" +
          "Screenshot: " + path + png() + "%n" +
          "Page source: " + path + html() + "%n" +
          "Timeout: 15 ms."));
  }

  @Test
  void pageObjectElementTextDoesNotMatch() {
    String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/pageObjectElementTextDoesNotMatch";
    assertThatThrownBy(() ->
      $(pageObject.header1).shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text 'expected text' \\{By.tagName: h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void pageObjectWrapperTextDoesNotMatch() {
    String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/pageObjectWrapperTextDoesNotMatch";
    assertThatThrownBy(() ->
      $(pageObject.header2).shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text 'expected text' \\{By.tagName: h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void selectOptionFromUnexistingList() {
    assertThatThrownBy(() ->
      $(pageObject.categoryDropdown).selectOption("SomeOption")
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {By.id: invalid_id}")
      .hasMessageContaining("Expected: exist");
  }

  @Test
  void clickUnexistingWrappedElement() {
    String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/clickUnexistingWrappedElement";
    assertThatThrownBy(() ->
      $(pageObject.categoryDropdown).click()
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageMatching(String.format("Element not found \\{By.id: invalid_id}%n" +
        "Expected: visible or transparent: visible or have css value opacity=0%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms.%n" +
        "Caused by: NoSuchElementException:.*"));
  }

  @Test
  void existingElementShouldNotExist() {
    String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/existingElementShouldNotExist";
    assertThatThrownBy(() ->
      $("h2").shouldNot(exist)
    )
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageMatching(String.format("Element should not exist \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void nonExistingElementShouldNotBeHidden() {
    String path = "http://ci.org/build/reports/tests/integration/errormessages/MissingElementTest/nonExistingElementShouldNotBeHidden";
    assertThatThrownBy(() ->
      $("h14").shouldNotBe(hidden)
    )
      .isInstanceOf(ElementNotFound.class)
      .hasMessageMatching(String.format("Element not found \\{h14}%n" +
        "Expected: not hidden%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms.%n" +
        "Caused by: NoSuchElementException:.*"));
  }

  @Test
  void clickingNonClickableElement() {
    assumeTrue(WebDriverRunner.isChrome());

    assertThatThrownBy(() ->
      $("#non-clickable-element a").shouldBe(visible).click()
    )
      .isInstanceOf(UIAssertionError.class)
      .hasMessageContainingAll(
        "is not clickable at point",
        "Other element would receive the click",
        "Screenshot: http://ci.org/build/reports/tests",
        "Page source: http://ci.org/build/reports/tests"
      );
  }

  private String png() {
    return "/\\d+\\.\\d+\\.png";
  }

  private String html() {
    return "/\\d+\\.\\d+\\.html";
  }

  private String pngOrHtml() {
    return "/\\d+\\.\\d+\\.(png|html)";
  }

  static class PageObject {
    @FindBy(tagName = "h2")
    SelenideElement header1;

    @FindBy(tagName = "h2")
    WebElement header2;

    @FindBy(id = "invalid_id")
    private WebElement categoryDropdown;
  }
}
