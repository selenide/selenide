package integration;

import com.codeborne.selenide.junit.ScreenShooter;
import org.junit.*;
import org.openqa.selenium.Dimension;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.openqa.selenium.net.PortProber.findFreePort;

public abstract class IntegrationTest {
  @Rule
  public ScreenShooter img = ScreenShooter.failedTests() ;

  private static int port;
  protected static LocalHttpServer server;

  @BeforeClass
  public static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (IntegrationTest.class) {
        port = findFreePort();
        server = new LocalHttpServer(port).start();
        System.setProperty("selenide.start-maximized", "false");
        System.out.println("START " + browser + " TESTS");
      }
    }
  }

  @AfterClass
  public static void restartUnstableWebdriver() {
    if (isIE() || isPhantomjs()) {
      closeWebDriver();
    }
  }

  protected void openFile(String fileName) {
    open("http://0.0.0.0:" + port + "/" + fileName);
    adjustBrowserWindowSize();
  }

  protected <T> T openFile(String fileName, Class<T> pageObjectClass) {
    T page = open("http://0.0.0.0:" + port + "/" + fileName, pageObjectClass);
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
