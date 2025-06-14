package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

final class ShadowDomDeepSelectorsInsideIFrameTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_with_shadow_dom_inside_iframe.html");
    timeout = 1000;
  }

  @Test
  void getTargetElementViaShadowHostInsideIFrame() {
    switchTo().frame("iframe_page");
    $(shadowDeepCss("#shadow-host p"))
      .shouldHave(text("Inside Shadow-DOM"));
  }

  @Test
  void setValueViaShadowDomInsideIFrame() {
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
