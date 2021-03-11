package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class DefaultClipboardTest extends IntegrationTest {

  @BeforeEach
  public void openTestPage() {
    openFile("clipboard.html");
  }

  @Disabled("Need configure X11 for github actions, not sure that it needs ")
  @Test
  public void getClipboard() {
    $("#myInput").should(Condition.attribute("value", "Hello World"));
    $("#my-button").should(Condition.visible).click();
    assertEquals("Hello World", Selenide.clipboard().getText(),"Clipboard data doesn't match");
    Selenide.switchTo().alert().accept();
  }

  @Disabled("Need configure X11 for github actions, not sure that it needs")
  @Test
  public void checkSetValue() {
    Selenide.clipboard().setText("111");
    assertEquals("111", Selenide.clipboard().getText(),"Clipboard data doesn't match");
  }

  @AfterAll
  public static void tearDown() {
    closeWebDriver();
  }

}
