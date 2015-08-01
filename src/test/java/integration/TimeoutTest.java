package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TimeoutTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void lookingForNonExistingElementShouldFailFast() {
    long start = System.nanoTime();
    try {
      getWebDriver().findElement(By.id("nonExistingElement"));
      fail("Looking for non-existing element should fail");
    } catch (NoSuchElementException expectedException) {
      long end = System.nanoTime();
      assertTrue("Looking for non-existing element took more than 1 ms: " + (end - start) / 1000000 + " ms.",
          end - start < 1000000000L);
    }
  }

  @Test
  public void timeoutShouldBeInMilliseconds() {
    try {
      $(By.xpath("//h16")).waitUntil(visible, 15);
    } catch (ElementNotFound expectedException) {
      assertTrue("Error message should contain timeout '15 ms', but received: " +
          expectedException.toString(), expectedException.toString().contains("15 ms"));
    }
  }

  @Test
  public void timeoutShouldBeFormattedInErrorMessage() {
    try {
      $(By.xpath("//h19")).waitUntil(visible, 1500);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expectedException) {
      assertTrue("Error message should contain timeout '1.500 s', but received: " +
          expectedException.toString(), expectedException.toString().contains("1.500 s"));
    }
  }

  @Test
  public void timeoutLessThanSecond() {
    try {
      $(By.xpath("//h18")).waitUntil(visible, 800);
      fail("Expected ElementNotFound");
    } catch (ElementNotFound expectedException) {
      assertTrue("Error message should contain timeout '800 ms', but received: " +
          expectedException.toString(), expectedException.toString().contains("800 ms"));
    }
  }
}
