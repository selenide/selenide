package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.atBottom;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;

class PageAtBottomTest extends IntegrationTest {
  @BeforeEach
  void createScrollablePage() {
    Assumptions.assumeFalse(isHtmlUnit(), "Scrolling not supported in htmlunit");
    openFile("empty.html");
    for (int i = 0; i < 200; i++) {
      Selenide.executeJavaScript(
        "var el = document.createElement(\"li\");" +
          "el.textContent=\"Element " + (i + 1) + "\";" +
          "document.body.appendChild(el);"
      );
    }
  }

  @Test
  void checkPageBottomIsReached() {
    Assertions.assertFalse(atBottom(), "oops, we shouldn't be at the bottom yet! " + printScrollParams());
    Selenide.executeJavaScript("return window.scrollTo(0, document.body.scrollHeight);");
    Assertions.assertTrue(atBottom(), "oops we should have reached the bottom already! " + printScrollParams());
  }

  private String printScrollParams() {
    return "\nwindow.scrollY=" + Selenide.executeJavaScript("return window.pageYOffset;") +
      "\nwindow.innerHeight=" + Selenide.executeJavaScript("return window.innerHeight;") +
      "\ndocument.body.scrollHeight=" + Selenide.executeJavaScript("return document.body.scrollHeight;");
  }
}
