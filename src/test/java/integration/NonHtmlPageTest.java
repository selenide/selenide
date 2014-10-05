package integration;

import org.junit.Test;

import static com.codeborne.selenide.WebDriverRunner.source;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class NonHtmlPageTest extends IntegrationTest {
  @Test
  public void canOpenNonHtmlPage() {
    openFile("hello_world.txt");
    assertThat(source(), containsString("Hello, WinRar!"));
  }
}
