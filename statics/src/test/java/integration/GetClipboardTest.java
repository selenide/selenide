package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class GetClipboardTest extends IntegrationTest {

  @BeforeEach
  public void openTestPage() {
    openFile("clipboard.html");
  }

  @Disabled("Need configure X11 for github actions, not sure that it needs")
  @Test
  public void getClipboard() {
    $("#myInput").should(Condition.attribute("value", "Hello World"));
    $("#my-button").should(Condition.visible).click();
    String clipboardString = Selenide.clipboard().getString();
    assertEquals("Hello World", clipboardString, "clipboard content doesn't match");
  }

  @AfterAll
  public static void tearDown() {
    closeWebDriver();
  }

}
