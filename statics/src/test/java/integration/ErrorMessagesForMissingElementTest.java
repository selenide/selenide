package integration;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.headless;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getElement;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ErrorMessagesForMissingElementTest extends IntegrationTest {
  private PageObject pageObject;
  private String reportsUrl;
  private String reportsFolder;

  @BeforeEach
  final void setTimeout() {
    timeout = 15;
    pageObject = openFile("page_with_selects_without_jquery.html", PageObject.class);
  }

  @BeforeEach
  void mockScreenshots() {
    reportsUrl = Configuration.reportsUrl;
    reportsFolder = Configuration.reportsFolder;
    Configuration.reportsUrl = "http://ci.org/";
    Configuration.reportsFolder = "build/reports/tests/EMFMET";
  }

  @AfterEach
  void restoreScreenshots() {
    Configuration.reportsUrl = reportsUrl;
    Configuration.reportsFolder = reportsFolder;
  }

  private boolean supportsScreenshots() {
    return !new Browser(browser, headless).isHtmlUnit();
  }

  @Test
  void elementNotFound() {
    try {
      $("h9").shouldHave(text("expected text"));
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound expected) {
      assertStartsWith("Element not found \\{h9\\}\n" +
        "Expected: text 'expected text'\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.\n" +
        "Caused by: NoSuchElementException:", expected);
      assertThat(expected.getScreenshot()).matches("http://ci.org/build/reports/tests/EMFMET" + pngOrHtml());
    }
  }

  private void assertStartsWith(String expectedMessageStart, Error error) {
    assertThat(error.toString())
      .as("Error should start with " + expectedMessageStart + ", but received: " + error)
      .matches(expectedMessageStart + ".*");
  }

  @Test
  void elementTextDoesNotMatch() {
    try {
      $("h2").shouldHave(text("expected text"));
      fail("Expected ElementShould");
    }
    catch (ElementShould expected) {
      assertThat(expected.toString()).matches("Element should have text 'expected text' \\{h2\\}\n" +
        "Element: '<h2>Dropdown list</h2>'\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.");
      assertThat(expected.getScreenshot()).matches("http://ci.org/build/reports/tests/EMFMET" + pngOrHtml());
    }
  }

  @Test
  void elementAttributeDoesNotMatch() {
    try {
      $("h2").shouldHave(attribute("name", "header"));
      fail("Expected ElementShould");
    }
    catch (ElementShould expected) {
      assertThat(expected.toString()).matches("Element should have attribute name=header \\{h2\\}\n" +
        "Element: '<h2>Dropdown list</h2>'\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.");
    }
  }

  @Test
  void wrapperTextDoesNotMatch() {
    try {
      $(getElement(By.tagName("h2"))).shouldHave(text("expected text"));
      fail("Expected ElementShould");
    }
    catch (ElementShould expected) {
      assertThat(expected.toString()).matches("Element should have text 'expected text' \\{By.tagName: h2\\}\n" +
        "Element: '<h2>Dropdown list</h2>'\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.");
    }
  }

  @Test
  void clickHiddenElement() {
    try {
      $("#theHiddenElement").click();
      fail("Expected ElementShould");
    }
    catch (ElementShould elementShouldExist) {
      assertThat(elementShouldExist.toString()).matches("Element should be visible \\{\\#theHiddenElement\\}\n" +
        "Element: '<div id=\"theHiddenElement\" displayed:false></div>'\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.");
      assertThat(elementShouldExist.getScreenshot()).matches("http://ci.org/build/reports/tests/EMFMET" + pngOrHtml());
    }
  }

  @Test
  void pageObjectElementTextDoesNotMatch() {
    try {
      $(pageObject.header1).shouldHave(text("expected text"));
      fail("Expected ElementShould");
    }
    catch (ElementShould expected) {
      assertThat(expected.toString()).matches("Element should have text 'expected text' \\{By.tagName: h2\\}\n" +
        "Element: '<h2>Dropdown list</h2>'\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.");
    }
  }

  @Test
  void pageObjectWrapperTextDoesNotMatch() {
    try {
      $(pageObject.header2).shouldHave(text("expected text"));
      fail("Expected ElementShould");
    }
    catch (ElementShould expected) {
      assertThat(expected.toString()).matches("Element should have text 'expected text' \\{By.tagName: h2\\}\n" +
        "Element: '<h2>Dropdown list</h2>'\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.");
    }
  }

  @Test
  void selectOptionFromUnexistingList() {
    try {
      $(pageObject.categoryDropdown).selectOption("SomeOption");
    }
    catch (ElementNotFound e) {
      assertContains(e, "Element not found \\{By.id: invalid_id\\}", "Expected: exist");
    }
  }

  private void assertContains(UIAssertionError e, String... expectedTexts) {
    for (String expectedText : expectedTexts) {
      assertThat(e.toString())
        .as("Text not found: " + expectedText + " in error message: " + e)
        .matches(Pattern.compile(".*" + expectedText + ".*", Pattern.DOTALL));
    }
  }

  @Test
  void clickUnexistingWrappedElement() {
    try {
      $(pageObject.categoryDropdown).click();
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound e) {
      assertStartsWith("Element not found \\{By.id: invalid_id\\}\n" +
        "Expected: visible or transparent\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.\n" +
        "Caused by: NoSuchElementException:", e);
    }
  }

  @Test
  void existingElementShouldNotExist() {
    try {
      $("h2").shouldNot(exist);
      fail("Expected ElementFound");
    }
    catch (ElementShouldNot e) {
      assertThat(e.toString()).matches("Element should not exist \\{h2\\}\n" +
        "Element: '<h2>Dropdown list</h2>'\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.");
    }
  }

  @Test
  void nonExistingElementShouldNotBeHidden() {
    try {
      $("h14").shouldNotBe(hidden);
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound e) {
      assertStartsWith("Element not found \\{h14\\}\n" +
        "Expected: not hidden\n" +
        (supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "") +
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n" +
        "Timeout: 15 ms.\n" +
        "Caused by: NoSuchElementException:", e);
    }
  }

  @Test
  void clickingNonClickableElement() {
    assumeTrue(WebDriverRunner.isChrome());

    try {
      $("#non-clickable-element a").shouldBe(visible).click();
      fail("Expected WebDriverException");
    }
    catch (UIAssertionError e) {
      assertContains(e, "is not clickable at point",
        "Other element would receive the click",
        supportsScreenshots() ? "Screenshot: http://ci.org/build/reports/tests/EMFMET" + png() + "\n" : "",
        "Page source: http://ci.org/build/reports/tests/EMFMET" + html() + "\n");
    }
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
