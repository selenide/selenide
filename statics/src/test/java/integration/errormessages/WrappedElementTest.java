package integration.errormessages;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class WrappedElementTest extends IntegrationTest {
  private PageObject pageObject;

  @BeforeEach
  void setTimeout() {
    timeout = 15;
    openFile("page_with_selects_without_jquery.html");
    pageObject = new PageObject(WebDriverRunner.getWebDriver());
  }

  @Test
  void pageObjectWrapperTextDoesNotMatch() {
    assertThatThrownBy(() ->
      $(pageObject.header).shouldHave(text("expected text"))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith(String.format("Element should have text 'expected text' {<h2 id>}%n" +
        "Element: '<h2>Dropdown list</h2>'"));
  }

  @Test
  void selectOptionFromUnexistingList() {
    assertThatThrownBy(() ->
      $(pageObject.categoryDropdown).selectOption("SomeOption")
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {Ups, failed to described the element [caused by: NoSuchElementException")
      .hasMessageContaining("Expected: exist");
  }

  @Test
  void selectOptionFromUnexistingList_namedElement() {
    assertThatThrownBy(() ->
      $(pageObject.categoryDropdown).as("Category list").selectOption("SomeOption")
    ).isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {Category list}")
      .hasMessageContaining("Expected: exist");
  }

  static class PageObject {
    @FindBy(tagName = "h2")
    private WebElement header;

    @FindBy(id = "invalid_id")
    private WebElement categoryDropdown;

    /**
     * Classic Selenium-style page object :(
     */
    PageObject(WebDriver webDriver) {
      PageFactory.initElements(webDriver, this);
    }
  }
}
