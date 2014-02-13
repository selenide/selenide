package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InvalidXPathTest {
  @Before
  public void openTestPageWithJQuery() {
    open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @Test(expected = InvalidSelectorException.class)
  public void usingInvalidXPathShouldThrowInvalidSelectorException() {
    $(By.xpath("##[id")).shouldNot(exist);
  }

  @Test
  public void lookingForMissingElementByXPathShouldFail() {
    try {
      $(By.xpath("//tagga")).should(exist);
      fail("Expected: ElementNotFound exception with cause");
    } catch (ElementNotFound expectedException) {
      assertTrue(expectedException.toString().contains("Element not found {By.xpath: //tagga}"));
      //assertTrue(expectedException.getCause() instanceof WebDriverException);
    }
  }
}
