package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class ClickRelativeTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    assumeFalse(isHtmlUnit());
    openFile("page_with_relative_click_position.html");
  }

  @Test
  void userCanClickElementWithOffsetPosition_withActions() {
    assumeFalse(isFirefox());
    Configuration.clickViaJs = false;

    $("#page").click(123, 321);

    $("#coords").should(matchText("(123, 321)"));
  }

  @Test
  void userCanClickElementWithOffsetPosition_withJavascript() {
    assumeFalse(isPhantomjs());
    Configuration.clickViaJs = true;

    $("#page").click(321, 123);

    $("#coords").should(matchText("(321, 123)"));
  }
}
