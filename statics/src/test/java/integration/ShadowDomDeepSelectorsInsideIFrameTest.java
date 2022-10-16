package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.assertj.core.api.Assumptions.assumeThat;

final class ShadowDomDeepSelectorsInsideIFrameTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_with_shadow_dom_inside_iframe.html");
  }

  @Test
  void getTargetElementViaShadowHostInsideIFrame() {
    switchTo().frame("iframe_page");
    $(shadowDeepCss("#shadow-host p"))
      .shouldHave(text("Inside Shadow-DOM"));
  }

  @Test
  void setValueViaShadowDomInsideIFrame() {
    // Firefox says that the input is "not reachable by keyboard" (inside shadow-dom)
    assumeThat(isFirefox()).isFalse();
    switchTo().frame("iframe_page");
    Configuration.fastSetValue = false;
    $(shadowDeepCss(".shadow-host input")).setValue("test").shouldHave(value("test"));
  }

  @Test
  void setFastValueViaShadowDomInsideIFrame() {
    switchTo().frame("iframe_page");
    Configuration.fastSetValue = true;
    $(shadowDeepCss("#shadow-host input")).setValue("test").shouldHave(value("test"));
  }
}
