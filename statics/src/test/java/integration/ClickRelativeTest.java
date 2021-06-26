package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Click with offset - calculates offset from the center of clicked element.
 * Element '#page' is 800x600 -> its center is 400x300.
 * Click to (400+123, 300+222) -> (523, 522)
 */
final class ClickRelativeTest extends IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(ClickRelativeTest.class);

  @BeforeEach
  void openTestPage() {
    openFile("page_with_relative_click_position.html");
  }

  @RepeatedTest(10)
  void userCanClickElementWithOffsetPosition_withActions() {
    Configuration.clickViaJs = false;

    log.info("{}", WebDriverRunner.driver().config());
    log.info("position: {}", WebDriverRunner.getWebDriver().manage().window().getPosition());
    log.info("size: {}", WebDriverRunner.getWebDriver().manage().window().getSize());
    log.info("title: {}", WebDriverRunner.getWebDriver().getTitle());
    log.info("url: {}", WebDriverRunner.getWebDriver().getCurrentUrl());

    $("#page").click(123, 222);

    $("#coords").shouldHave(exactText("(523, 522)"));
  }

  @RepeatedTest(10)
  void userCanClickElementWithOffsetPosition_withJavascript() {
    Configuration.clickViaJs = true;

    $("#page").click(123, 222);

    $("#coords").shouldHave(exactText("(523, 522)"));
  }

  @RepeatedTest(10)
  void userCanClickElementWithOffsetPositions_withClickOptionJS() {
    Configuration.clickViaJs = false;

    $("#page").click(usingJavaScript().offset(123, 222));

    $("#coords").shouldHave(exactText("(523, 522)"));
  }

  @RepeatedTest(10)
  void userCanClickElementWithOffsetXPosition_withClickOptionJS() {
    Configuration.clickViaJs = false;

    $("#page").click(usingJavaScript().offsetX(123));

    $("#coords").shouldHave(exactText("(523, 300)"));
  }

  @RepeatedTest(10)
  void screenshotIsTaken_ifClickWithOffset_getsOutsideOfElement() {
    Configuration.timeout = 123;

    assertThatThrownBy(() -> $("#page").click(usingDefaultMethod().offsetX(9999999)))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageContaining("MoveTargetOutOfBoundsException")
      .hasMessageContaining("out of bounds")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 123 ms.")
      .hasCauseInstanceOf(MoveTargetOutOfBoundsException.class);
  }
}
