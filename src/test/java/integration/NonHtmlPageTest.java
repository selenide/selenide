package integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.WebDriverRunner.source;

class NonHtmlPageTest extends IntegrationTest {
  @Test
  void canOpenNonHtmlPage() {
    openFile("hello_world.txt");
    String source = source();
    Assertions.assertTrue(source.contains("Hello, WinRar!"), "Actual source: " + source);
  }
}
