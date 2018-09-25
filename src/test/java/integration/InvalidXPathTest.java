package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;

import static com.codeborne.selenide.Condition.exist;

class InvalidXPathTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void usingInvalidXPathShouldThrowInvalidSelectorException() {
    assertThatThrownBy(() -> $(By.xpath("##[id")).shouldNot(exist))
      .isInstanceOf(InvalidSelectorException.class);
  }

  @Test
  void lookingForMissingElementByXPathShouldFail() {
    try {
      $(By.xpath("//tagga")).should(exist);
      fail("Expected: ElementNotFound exception with cause");
    } catch (ElementNotFound expectedException) {
      assertThat(expectedException.toString())
        .contains("Element not found {By.xpath: //tagga}");
    }
  }
}
