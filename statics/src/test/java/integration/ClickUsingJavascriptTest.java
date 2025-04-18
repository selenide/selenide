package integration;

import com.codeborne.selenide.ClickMethod;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.ClickOptions.using;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.Keys.ALT;
import static org.openqa.selenium.Keys.SHIFT;

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

  @ParameterizedTest
  @EnumSource(ClickMethod.class)
  void userCanClick_whileHoldingKeys(ClickMethod clickMethod) {
    openFile("page_with_double_clickable_button.html");
    $("h2").shouldHave(exactText("Status: not clicked yet"));

    $("#double-clickable-button").click(using(clickMethod).holdingKeys(ALT));
    $("h2").shouldHave(exactText("Status: clicked+alt"));

    $("#double-clickable-button").click(using(clickMethod).holdingKeys(SHIFT));
    $("h2").shouldHave(exactText("Status: clicked+shift"));

    $("#double-clickable-button").click(using(clickMethod).holdingKeys(ALT, SHIFT));
    $("h2").shouldHave(exactText("Status: clicked+shift+alt"));
  }

  public static class MyListener implements WebDriverListener {
    @Override
    public void beforeClick(WebElement element) {
      clickedElements.add(element);
    }
  }
}
