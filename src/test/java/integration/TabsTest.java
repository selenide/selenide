package integration;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.util.Set;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;

public class TabsTest extends IntegrationTest {
  @Before
  public void setUp() {
    openFile("page_with_tabs.html");
  }

  @Test
  public void userCanBrowseTabs_webdriver_api() {
    openFile("page_with_tabs.html");

    WebDriver driver = WebDriverRunner.getWebDriver();
    String windowHandle = driver.getWindowHandle();

    $(byText("Page1: uploads")).click();

    $("h1").shouldHave(text("Tabs"));

    Set<String> windowHandles = driver.getWindowHandles();
    windowHandles.remove(windowHandle);

    driver.switchTo().window(windowHandles.iterator().next());
    $("h1").shouldHave(text("File uploads"));

    driver.switchTo().window(windowHandle);
    $("h1").shouldHave(text("Tabs"));
  }

  @Test
  public void canSwitchToWindowByTitle() {
    $(byText("Page2: alerts")).click();
    $(byText("Page1: uploads")).click();
    $(byText("Page3: jquery")).click();

    $("h1").shouldHave(text("Tabs"));

    Selenide.switchToWindow("Test::alerts");
    $("h1").shouldHave(text("Page with alerts"));

    Selenide.switchToWindow("Test::jquery");
    $("h1").shouldHave(text("Page with JQuery"));

    Selenide.switchToWindow("Test::uploads");
    $("h1").shouldHave(text("File uploads"));

    Selenide.switchToWindow("Test::tabs");
    $("h1").shouldHave(text("Tabs"));
  }

  @After
  public void tearDown() {
    close();
  }
}
