package integration;

import org.junit.Test;

import static com.codeborne.selenide.WebDriverRunner.source;
import static org.junit.Assert.assertEquals;

public class NonHtmlPageTest extends IntegrationTest {
  @Test
  public void canOpenNonHtmlPage() {
    openFile("hello_world.txt");
    assertEquals("Hello, WinRar!", source());
  }
}
