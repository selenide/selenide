package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;

class ClickUsingJavascriptTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    Configuration.clickViaJs = true;
  }

  @Test
  void userCanClickUsingJavaScript_itCanBeUsefulWithIE() {
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).click();
    $(By.name("rememberMe")).shouldBe(selected);
  }
}
