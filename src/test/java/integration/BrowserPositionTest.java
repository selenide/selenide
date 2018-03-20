package integration;

import com.codeborne.selenide.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Point;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

public class BrowserPositionTest extends IntegrationTest {
  @Before
  @After
  public void closeBrowser() {
    assumeFalse(isHeadless());
    close();
  }

  @Test
  public void ableToSetBrowserPosition() {
    Configuration.browserPosition = "30x60";

    open("/start_page.html");

    assertEquals(new Point(30, 60), getWebDriver().manage().window().getPosition());
  }

  @Test
  public void anotherBrowserPosition() {
    Configuration.browserPosition = "110x100";

    open("/start_page.html");

    assertEquals(new Point(110, 100), getWebDriver().manage().window().getPosition());
  }
}
