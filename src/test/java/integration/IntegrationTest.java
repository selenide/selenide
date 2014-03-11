package integration;

import org.junit.BeforeClass;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.net.PortProber.findFreePort;

public abstract class IntegrationTest {
  private static int port;
  private static LocalHttpServer server;

  @BeforeClass
  public static void runLocalHttpServer() throws IOException {
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
