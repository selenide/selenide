package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;

final class ClickUsingJavascriptTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    Configuration.clickViaJs = false;
  }

  @Test
  void userCanClickUsingJavaScript_viaGlobalConfig_itCanBeUsefulWithIE() {
    Configuration.clickViaJs = true;
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).click();
    $(By.name("rememberMe")).shouldBe(selected);
  }

  @Test
  void userCanClickUsingJavaScript_viaClickOptions() {
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).click(usingJavaScript());
    $(By.name("rememberMe")).shouldBe(selected);
  }
}
