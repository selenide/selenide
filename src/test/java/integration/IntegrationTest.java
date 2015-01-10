package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit.ScreenShooter;
import com.codeborne.selenide.logevents.PrettyReportCreator;
import org.junit.*;
import org.junit.rules.TestRule;
import org.openqa.selenium.Dimension;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.openqa.selenium.net.PortProber.findFreePort;

public abstract class IntegrationTest {
  @Rule
  public ScreenShooter img = ScreenShooter.failedTests() ;

  @Rule
  public TestRule prettyReportCreator = new PrettyReportCreator();

  private static int port;
  protected static LocalHttpServer server;

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
  public void setBaseUrl() {
    Configuration.baseUrl = "http://0.0.0.0:" + port;
    System.setProperty("selenide.start-maximized", "false");
  }

  @AfterClass
  public static void restartUnstableWebdriver() {
    if (isIE() || isPhantomjs()) {
      closeWebDriver();
    }
  }

  protected void openFile(String fileName) {
    open("/" + fileName);
    adjustBrowserWindowSize();
  }

  protected <T> T openFile(String fileName, Class<T> pageObjectClass) {
    T page = open("/" + fileName, pageObjectClass);
    adjustBrowserWindowSize();
    return page;
  }

  private void adjustBrowserWindowSize() {
    getWebDriver().manage().window().setSize(new Dimension(1024, 768));
    System.out.println("Using browser " + browser + " with window size: " + getWebDriver().manage().window().getSize());
  }


  private long defaultTimeout;

  @Before
  public final void rememberTimeout() {
    defaultTimeout = timeout;
  }

  @After
  public final void restoreTimeout() {
    timeout = defaultTimeout;
  }
}
