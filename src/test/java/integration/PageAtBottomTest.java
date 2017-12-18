package integration;

import com.codeborne.selenide.Selenide;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.atBottom;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class PageAtBottomTest extends IntegrationTest {
  @Before
  public void createScrollablePage() {
    assumeFalse("Scrolling not supported in htmlunit", isHtmlUnit());
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
  public void checkPageBottomIsReached() {
    assertFalse("oops, we shouldn't be at the bottom yet! " + printScrollParams(), atBottom());
    Selenide.executeJavaScript("return window.scrollTo(0, document.body.scrollHeight);");
    assertTrue("oops we should have reached the bottom already! " + printScrollParams(), atBottom());
  }

  private String printScrollParams() {
    return "\nwindow.scrollY=" + Selenide.executeJavaScript("return window.scrollY;") +
          "\nwindow.innerHeight=" + Selenide.executeJavaScript("return window.innerHeight;") +
          "\ndocument.body.scrollHeight=" + Selenide.executeJavaScript("return document.body.scrollHeight;");
  }
}
