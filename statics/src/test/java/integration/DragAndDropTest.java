package integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.DragAndDropOptions.to;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.assertj.core.api.Assumptions.assumeThat;

public class DragAndDropTest extends IntegrationTest {

  @BeforeEach
  public void openTestPage() {
    openFile("draggable.html");
  }

  @Test
  public void dragAndDrop_using_js() {
    $("#drag1").dragAndDrop(to("#div2").usingJS());
    $("#div2").$("#drag1").should(appear);
  }

  @Test
  public void dragAndDrop_toWebElement_using_js() {
    $("#drag1").dragAndDrop(to($("#div2")).usingJS());
    $("#div2").$("#drag1").should(appear);
  }

  @Test
  public void dragAndDrop_toWebElement_using_actions() {
    assumeThat(isFirefox()).isFalse();
    $("#drag1").dragAndDrop(to($("#div2")).usingSeleniumActions());
    $("#div2").$("#drag1").should(appear);
  }

  @Test
  public void dragAndDrop_toCssSelector_using_actions() {
    assumeThat(isFirefox()).isFalse();
    $("#drag1").dragAndDrop(to("#div2").usingSeleniumActions());
    $("#div2").$("#drag1").should(appear);
  }

  @AfterAll
  public static void tearDown() {
    closeWebDriver();
  }
}
