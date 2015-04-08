package integration;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;

public class ClickUsingJavascriptTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    Configuration.clickViaJs = true;
  }

  @Test
  public void userCanClickUsingJavaScript_itCanBeUsefulWithIE() {
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).click();
    $(By.name("rememberMe")).shouldBe(selected);
  }
}
