package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.UIAssertionError;
import integration.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static integration.errormessages.Helper.getReportsFolder;
import static integration.errormessages.Helper.screenshot;
import static integration.errormessages.Helper.webElementNotFound;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assumptions.assumeThat;

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
    Configuration.reportsFolder = getReportsFolder();
  }

  @AfterEach
  void restoreScreenshots() {
    Configuration.reportsUrl = reportsUrl;
    Configuration.reportsFolder = reportsFolder;
  }

  @Test
  void elementNotFound() {
    String path = path("elementNotFound");
    UIAssertionError e = catchThrowableOfType(() -> $("#h9").shouldHave(text("expected text")), UIAssertionError.class);
    assertThat(e)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageMatching(String.format("Element not found \\{#h9}%n" +
        "Expected: text \"expected text\"%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.%n" +
        "Caused by: NoSuchElementException:.*", path, png(), path, html()))
      .has(screenshot(path("elementNotFound")))
      .cause()
      .isInstanceOf(NoSuchElementException.class)
      .is(webElementNotFound("#h9"));
    assertThat(e.getActual()).isNull();
    assertThat(e.getExpected()).isNull();
  }

  @Test
  void elementTextDoesNotMatch() {
    String path = path("elementTextDoesNotMatch");
    UIAssertionError e = catchThrowableOfType(() ->
      $("h2").shouldHave(text("expected text")), UIAssertionError.class
    );
    assertThat(e)
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text \"expected text\" \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: text=\"Dropdown list\"%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.", path, png(), path, html()));
    assertThat(e.getActual().getStringRepresentation()).isEqualTo("text=\"Dropdown list\"");
    assertThat(e.getExpected().getStringRepresentation()).isEqualTo("text \"expected text\"");
  }

  @Test
  void elementAttributeDoesNotMatch() {
    String path = path("elementAttributeDoesNotMatch");
    UIAssertionError e = catchThrowableOfType(() ->
      $("h2").shouldHave(attribute("name", "header")), UIAssertionError.class
    );
    assertThat(e)
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have attribute name=\"header\" \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: name=\"\"%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.", path, png(), path, html()));
    assertThat(e.getActual().getStringRepresentation()).isEqualTo("name=\"\"");
    assertThat(e.getExpected().getStringRepresentation()).isEqualTo("attribute name=\"header\"");
  }

  @Test
  void wrapperTextDoesNotMatch() {
    String path = path("wrapperTextDoesNotMatch");
    UIAssertionError e = catchThrowableOfType(() ->
      $(element(By.tagName("h2"))).shouldHave(text("expected text")), UIAssertionError.class
    );
    assertThat(e)
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text \"expected text\" \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: text=\"Dropdown list\"%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.", path, png(), path, html()));
    assertThat(e.getActual().getStringRepresentation()).isEqualTo("text=\"Dropdown list\"");
    assertThat(e.getExpected().getStringRepresentation()).isEqualTo("text \"expected text\"");
  }

  @Test
  void clickHiddenElement() {
    String path = path("clickHiddenElement");
    UIAssertionError e = catchThrowableOfType(() ->
      $("#theHiddenElement").click(), UIAssertionError.class
    );
    assertThat(e)
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format(
        "Element should be clickable: interactable and enabled \\{#theHiddenElement}%n" +
          "Element: '<div id=\"theHiddenElement\" displayed:false></div>'%n" +
          "Actual value: hidden, opacity=1%n" +
          "Screenshot: %s%s%n" +
          "Page source: %s%s%n" +
          "Timeout: 15 ms.", path, png(), path, html()));
    assertThat(e.getActual().getStringRepresentation()).isEqualTo("hidden, opacity=1");
    assertThat(e.getExpected().getStringRepresentation()).isEqualTo("clickable: interactable and enabled");
  }

  @Test
  void pageObjectElementTextDoesNotMatch() {
    String path = path("pageObjectElementTextDoesNotMatch");
    UIAssertionError e = catchThrowableOfType(() ->
      $(pageObject.header1).shouldHave(text("expected text")), UIAssertionError.class
    );
    assertThat(e)
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text \"expected text\" \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: text=\"Dropdown list\"%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.", path, png(), path, html()));
    assertThat(e.getActual().getStringRepresentation()).isEqualTo("text=\"Dropdown list\"");
    assertThat(e.getExpected().getStringRepresentation()).isEqualTo("text \"expected text\"");
  }

  @Test
  void pageObjectWrapperTextDoesNotMatch() {
    String path = path("pageObjectWrapperTextDoesNotMatch");
    UIAssertionError e = catchThrowableOfType(() ->
      $(pageObject.header2).shouldHave(text("expected text")), UIAssertionError.class
    );
    assertThat(e)
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text \"expected text\" \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: text=\"Dropdown list\"%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.", path, png(), path, html()));
    assertThat(e.getActual().getStringRepresentation()).isEqualTo("text=\"Dropdown list\"");
    assertThat(e.getExpected().getStringRepresentation()).isEqualTo("text \"expected text\"");
  }

  @Test
  void selectOptionFromMissingList() {
    UIAssertionError e = catchThrowableOfType(() ->
      $(pageObject.categoryDropdown).selectOption("SomeOption"), UIAssertionError.class
    );
    assertThat(e)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {#invalid_id}")
      .hasMessageContaining("Expected: exist");
    assertThat(e.getActual()).isNull();
    assertThat(e.getExpected()).isNull();
  }

  @Test
  void clickMissingWrappedElement() {
    String path = path("clickMissingWrappedElement");
    UIAssertionError e = catchThrowableOfType(() ->
      $(pageObject.categoryDropdown).click(), UIAssertionError.class
    );
    assertThat(e).isInstanceOf(ElementNotFound.class)
      .hasMessageMatching(String.format("Element not found \\{#invalid_id}%n" +
        "Expected: clickable: interactable and enabled%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.%n" +
        "Caused by: NoSuchElementException:.*", path, png(), path, html()));
    assertThat(e.getActual()).isNull();
    assertThat(e.getExpected()).isNull();
  }

  @Test
  void existingElementShouldNotExist() {
    String path = path("existingElementShouldNotExist");
    UIAssertionError e = catchThrowableOfType(() ->
      $("h2").shouldNot(exist), UIAssertionError.class);

    assertThat(e)
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageMatching(String.format("Element should not exist \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: exists%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.", path, png(), path, html()));
    assertThat(e.getActual().getStringRepresentation()).isEqualTo("exists");
    assertThat(e.getExpected().getStringRepresentation()).isEqualTo("not exist");
  }

  @Test
  void nonExistingElementShouldNotBeHidden() {
    String path = path("nonExistingElementShouldNotBeHidden");
    UIAssertionError e = catchThrowableOfType(() ->
      $("h14").shouldNotBe(hidden), UIAssertionError.class);

    assertThat(e)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageMatching(String.format("Element not found \\{h14}%n" +
        "Expected: not hidden%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.%n" +
        "Caused by: NoSuchElementException:.*", path, png(), path, html()));

    assertThat(e.getActual()).isNull();
    assertThat(e.getExpected()).isNull();
  }

  @Test
  void clickingNonClickableElement() {
    assumeThat(isChrome()).isTrue();

    assertThatThrownBy(() ->
      $("#non-clickable-element a").shouldBe(visible).click()
    )
      .isInstanceOf(UIAssertionError.class)
      .hasMessageContainingAll(
        "is not clickable at point",
        "Other element would receive the click",
        "Screenshot: " + path("clickingNonClickableElement"),
        "Page source: " + path("clickingNonClickableElement")
      );
  }

  private String png() {
    return "/\\d+\\.\\d+\\.png";
  }

  private String html() {
    return "/\\d+\\.\\d+\\.html";
  }

  static class PageObject {
    @FindBy(tagName = "h2")
    SelenideElement header1;

    @FindBy(tagName = "h2")
    WebElement header2;

    @FindBy(id = "invalid_id")
    private WebElement categoryDropdown;
  }

  private static String path(String testName) {
    return Helper.path("MissingElementTest", testName);
  }
}
