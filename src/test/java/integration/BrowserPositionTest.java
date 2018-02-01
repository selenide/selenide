package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Point;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assume.assumeFalse;

public class BrowserPositionTest extends IntegrationTest {
  String position;
  Point point;

  @Before
  public void prepareTestData() {
    Configuration.browser = WebDriverRunner.CHROME;

    position = "30x60";
    String[] coordinates = position.split("x");
    int x = Integer.parseInt(coordinates[0]);
    int y = Integer.parseInt(coordinates[1]);
    point = new Point(x, y);
  }

  @After
  public void closeBrowser() {
    close();
  }

  @Test
  public void ableToSetBrowserPosition() {
    assumeFalse(isHtmlUnit());

    Configuration.browserPosition = position;

    open("/start_page.html");

    assertEquals(point, WebDriverRunner.getWebDriver().manage().window().getPosition());
  }

  @Test
  public void ableToOpenBrowserWithoutPosition() {
    assumeFalse(isHtmlUnit());

    open("/start_page.html");

    assertNotEquals(point, WebDriverRunner.getWebDriver().manage().window().getPosition());
  }
}
