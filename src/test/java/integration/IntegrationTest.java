package integration;

import com.codeborne.selenide.junit.ScreenShooter;
import org.junit.BeforeClass;
import org.junit.Rule;

import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.net.PortProber.findFreePort;

public abstract class IntegrationTest {
  @Rule
  public ScreenShooter img = ScreenShooter.failedTests() ;

  private static int port;
  private static LocalHttpServer server;

  @BeforeClass
  public static void runLocalHttpServer() throws Exception {
    if (server == null) {
      synchronized (IntegrationTest.class) {
        port = findFreePort();
        server = new LocalHttpServer(port).start();
      }
    }
  }

  protected void openFile(String fileName) {
    open("http://localhost:" + port + "/" + fileName);
  }

  protected <T> T openFile(String fileName, Class<T> pageObjectClass) {
    return open("http://localhost:" + port + "/" + fileName, pageObjectClass);
  }
}
