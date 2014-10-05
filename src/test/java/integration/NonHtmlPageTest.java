package integration;

import org.junit.Test;

import static com.codeborne.selenide.WebDriverRunner.source;
import static org.junit.Assert.assertTrue;

public class NonHtmlPageTest extends IntegrationTest {
  @Test
  public void canOpenNonHtmlPage() {
    openFile("hello_world.txt");
    String source = source();
    assertTrue("Actual source: " + source, source.contains("Hello, WinRar!"));
  }
}
