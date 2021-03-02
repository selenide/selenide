package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;


public class ClipboardServiceTest extends IntegrationTest {

  @BeforeEach
  public void openTestPage() {
    openFile("clipboard.html");
  }

  @Disabled("Need configure X11 for github actions, not sure that it needs")
  @Test
  public void getClipboard() {
    $("#myInput").should(Condition.attribute("value", "Hello World"));
    $("#my-button").should(Condition.visible).click();
    Selenide.clipboard().shouldBeText("Hello World");
    Selenide.switchTo().alert().accept();
  }

  @Disabled("Need configure X11 for github actions, not sure that it needs")
  @Test
  public void checkSetValue() {
    Selenide.clipboard().setValue("111");
    Selenide.clipboard().shouldBeText("111");
  }

  @AfterAll
  public static void tearDown() {
    closeWebDriver();
  }

}
