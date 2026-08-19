package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class CloudScreenshotTest {
  @Test
  void todo() {
    System.out.println("Hello");
    Selenide.open("https://the-internet.herokuapp.com/nested_frames");

//        Selenide.switchTo().frame($(By.name("frame-top")));
//        Selenide.switchTo().frame($(By.name("frame-middle")));
    Selenide.switchTo().frame($(By.name("frame-bottom")));
    String screenshot = Selenide.screenshot("kurver-iframe.png");
    System.out.println("XXX: " + screenshot);
  }
}
