package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;

class InvalidXPathTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void usingInvalidXPathShouldThrowInvalidSelectorException() {
    Assertions.assertThrows(InvalidSelectorException.class,
      () -> $(By.xpath("##[id")).shouldNot(exist));
  }

  @Test
  void lookingForMissingElementByXPathShouldFail() {
    try {
      $(By.xpath("//tagga")).should(exist);
      Assertions.fail("Expected: ElementNotFound exception with cause");
    } catch (ElementNotFound expectedException) {
      Assertions.assertTrue(expectedException.toString().contains("Element not found {By.xpath: //tagga}"));
    }
  }
}
