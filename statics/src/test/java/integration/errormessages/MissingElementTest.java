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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    assertThatThrownBy(() -> $("#h9").shouldHave(text("expected text")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageMatching(String.format("Element not found \\{#h9}%n" +
        "Expected: text \"expected text\"%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms.%n" +
        "Caused by: NoSuchElementException:.*"))
      .has(screenshot(path("elementNotFound")))
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .is(webElementNotFound("#h9"));
  }

  @Test
  void elementTextDoesNotMatch() {
    String path = path("elementTextDoesNotMatch");
    assertThatThrownBy(() ->
      $("h2").shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text \"expected text\" \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: text=\"Dropdown list\"%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void elementAttributeDoesNotMatch() {
    String path = path("elementAttributeDoesNotMatch");
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
    String path = path("wrapperTextDoesNotMatch");
    assertThatThrownBy(() ->
      $(element(By.tagName("h2"))).shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text \"expected text\" \\{By.tagName: h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: text=\"Dropdown list\"%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void clickHiddenElement() {
    String path = path("clickHiddenElement");
    assertThatThrownBy(() ->
      $("#theHiddenElement").click()
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format(
        "Element should be clickable: interactable and enabled \\{#theHiddenElement}%n" +
          "Element: '<div id=\"theHiddenElement\" displayed:false></div>'%n" +
          "Actual value: hidden, opacity=1%n" +
          "Screenshot: " + path + png() + "%n" +
          "Page source: " + path + html() + "%n" +
          "Timeout: 15 ms."));
  }

  @Test
  void pageObjectElementTextDoesNotMatch() {
    String path = path("pageObjectElementTextDoesNotMatch");
    assertThatThrownBy(() ->
      $(pageObject.header1).shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text \"expected text\" \\{By.tagName: h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: text=\"Dropdown list\"%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void pageObjectWrapperTextDoesNotMatch() {
    String path = path("pageObjectWrapperTextDoesNotMatch");
    assertThatThrownBy(() ->
      $(pageObject.header2).shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageMatching(String.format("Element should have text \"expected text\" \\{By.tagName: h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: text=\"Dropdown list\"%n" +
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
    String path = path("clickUnexistingWrappedElement");
    assertThatThrownBy(() ->
      $(pageObject.categoryDropdown).click()
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageMatching(String.format("Element not found \\{By.id: invalid_id}%n" +
        "Expected: clickable: interactable and enabled%n" +
        "Screenshot: %s%s%n" +
        "Page source: %s%s%n" +
        "Timeout: 15 ms.%n" +
        "Caused by: NoSuchElementException:.*", path, png(), path, html()));
  }

  @Test
  void existingElementShouldNotExist() {
    String path = path("existingElementShouldNotExist");
    assertThatThrownBy(() ->
      $("h2").shouldNot(exist)
    )
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageMatching(String.format("Element should not exist \\{h2}%n" +
        "Element: '<h2>Dropdown list</h2>'%n" +
        "Actual value: exists%n" +
        "Screenshot: " + path + png() + "%n" +
        "Page source: " + path + html() + "%n" +
        "Timeout: 15 ms."));
  }

  @Test
  void nonExistingElementShouldNotBeHidden() {
    String path = path("nonExistingElementShouldNotBeHidden");
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
