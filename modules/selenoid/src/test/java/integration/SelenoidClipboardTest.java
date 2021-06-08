package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelenoidClipboardTest {

  @BeforeEach
  public void prepare() {
    Configuration.remote = "http://localhost:4444/wd/hub";
    open("https://www.w3schools.com/howto/howto_js_copy_clipboard.asp");
    $(".snigel-cmp-framework").shouldBe(visible);
    executeJavaScript("const popup = document.getElementById('snigel-cmp-framework'); popup.parentNode.removeChild(popup);");
  }

  @Test
  public void getClipboardContent() {
    $("#myInput").should(Condition.attribute("value", "Hello World"));
    $("[onclick='myFunction()']").should(Condition.visible).click();
    assertEquals("Hello World", Selenide.clipboard().getText(), "clipboard content doesn't match");
  }

  @Test
  public void setClipboardContent() {
    Selenide.clipboard().setText("John Wick");
    assertEquals("John Wick", Selenide.clipboard().getText(), "clipboard content doesn't match");
  }

  @AfterEach
  public void tearDown() {
    closeWebDriver();
  }
}
