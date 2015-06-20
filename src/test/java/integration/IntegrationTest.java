package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit.ScreenShooter;
import com.codeborne.selenide.logevents.PrettyReportCreator;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.*;
import static java.lang.Math.max;
import static org.openqa.selenium.net.PortProber.findFreePort;

public abstract class IntegrationTest {
  @Rule
  public ScreenShooter img = ScreenShooter.failedTests() ;

  @Rule
  public TestRule prettyReportCreator = new PrettyReportCreator();
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static int port;
  protected static LocalHttpServer server;
  private long defaultTimeout;
  protected static long averageSeleniumCommandDuration = -1;

  @BeforeClass
  public static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (IntegrationTest.class) {
        port = findFreePort();
        server = new LocalHttpServer(port).start();
        System.out.println("START " + browser + " TESTS");
      }
    }
  }

  @Before
  public void restartReallyUnstableBrowsers() {
    if (isSafari()) {
      closeWebDriver();
    }
  }

  @Before
  public void resetSettings() {
    Configuration.baseUrl = "https://127.0.0.1:" + port;
    Configuration.reportsFolder = "build/reports/tests/" + Configuration.browser;
    fastSetValue = false;
    startMaximized = false;
  }

  @AfterClass
  public static void restartUnstableWebdriver() {
    if (isIE() || isPhantomjs()) {
      closeWebDriver();
    }
  }

  protected void openFile(String fileName) {
    measureSeleniumCommandDuration();
    open("/" + fileName + "?" + averageSeleniumCommandDuration);
  }

  protected <T> T openFile(String fileName, Class<T> pageObjectClass) {
    measureSeleniumCommandDuration();
    return open("/" + fileName + "?" + averageSeleniumCommandDuration, pageObjectClass);
  }

  @Before
  public final void rememberTimeout() {
    defaultTimeout = timeout;
  }

  @After
  public final void restoreDefaultProperties() {
    timeout = defaultTimeout;
    clickViaJs = false;
  }

  private void measureSeleniumCommandDuration() {
    if (averageSeleniumCommandDuration < 0) {
      open("/start_page.html");
      long start = System.currentTimeMillis();
      try {
        WebDriver driver = getWebDriver();
        driver.findElement(By.tagName("h1")).isDisplayed();
        driver.findElement(By.tagName("h1")).isEnabled();
        driver.findElement(By.tagName("body")).findElement(By.tagName("h1"));
        driver.findElement(By.tagName("h1")).getText();
        averageSeleniumCommandDuration = max(30, (System.currentTimeMillis() - start) / 4);
        System.out.println("Average selenium command duration for " + browser + ": " +
            averageSeleniumCommandDuration + " ms.");
      }
      catch (WebDriverException e) {
        System.err.println("Failed to calculate average selenium command duration. Using 100 by default.");
        averageSeleniumCommandDuration = 100;
      }
    }
  }
}
