package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static org.junit.Assert.*;

public class SelenideElementTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void selenideElementImplementsWrapsElement() {
    WebElement wrappedElement = $("#login").getWrappedElement();
    assertNotNull(wrappedElement);
    assertEquals("login", wrappedElement.getAttribute("id"));
  }

  @Test
  public void selenideElementImplementsWrapsWebdriver() {
    WebDriver wrappedDriver = $("#login").getWrappedDriver();
    assertNotNull(wrappedDriver);
    String currentUrl = wrappedDriver.getCurrentUrl();
    assertTrue("Current URL is " + currentUrl,
        currentUrl.contains("page_with_selects_without_jquery.html"));
  }
}
