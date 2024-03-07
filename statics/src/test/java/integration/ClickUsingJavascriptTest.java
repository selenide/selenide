package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.assertj.core.api.Assertions.assertThat;

final class ClickUsingJavascriptTest extends IntegrationTest {
  private static final List<WebElement> clickedElements = new ArrayList<>();
  private static final WebDriverListener listener = new MyListener();

  @BeforeAll
  static void beforeAll() {
    closeWebDriver();
    WebDriverRunner.addListener(listener);
  }

  @AfterAll
  static void afterAll() {
    closeWebDriver();
    WebDriverRunner.removeListener(listener);
  }

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    Configuration.clickViaJs = false;
  }

  @Test
  void userCanClickUsingJavaScript_viaGlobalConfig() {
    Configuration.clickViaJs = true;
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).click();
    $(By.name("rememberMe")).shouldBe(selected);
    assertThat(clickedElements).isEmpty();
  }

  @Test
  void userCanClickUsingJavaScript_viaClickOptions() {
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).click(usingJavaScript());
    $(By.name("rememberMe")).shouldBe(selected);
    assertThat(clickedElements).isEmpty();
  }

  @Test
  void userCanClickUsingDefaultMethod() {
    Configuration.clickViaJs = false;
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).click();
    $(By.name("rememberMe")).shouldBe(selected);
    assertThat(clickedElements).hasSize(1);
  }

  public static class MyListener implements WebDriverListener {
    @Override
    public void beforeClick(WebElement element) {
      clickedElements.add(element);
    }
  }
}
