package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.shadowRoot;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class ShadowDomInsideIFrameTest2 extends IntegrationTest {
  @BeforeEach
  void setUp() {
    openFile("page_with_shadow_dom_inside_iframe.html");
  }

  @Test
  void getTargetElementViaShadowHostInsideIFrame() {
    switchTo().frame("iframe_page");
    $(shadowRoot("#shadow-host", "p"))
      .shouldHave(text("Inside Shadow-DOM"));
  }

  @Test
  void setValueViaShadowDomInsideIFrame() {
    // Firefox says that the input is "not reachable by keyboard" (inside shadow-dom)
    assumeFalse(isFirefox());
    switchTo().frame("iframe_page");
    Configuration.fastSetValue = false;
    $(shadowRoot("#shadow-host", "input")).setValue("test").shouldHave(value("test"));
  }

  @Test
  void setFastValueViaShadowDomInsideIFrame() {
    switchTo().frame("iframe_page");
    Configuration.fastSetValue = true;
    $(shadowRoot("#shadow-host", "input")).setValue("test").shouldHave(value("test"));
  }
}
