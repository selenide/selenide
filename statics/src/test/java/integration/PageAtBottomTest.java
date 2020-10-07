package integration;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.atBottom;
import static org.assertj.core.api.Assertions.assertThat;

final class PageAtBottomTest extends IntegrationTest {
  @BeforeEach
  void createScrollablePage() {
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
    assertThat(atBottom())
      .withFailMessage("oops, we shouldn't be at the bottom yet! " + printScrollParams())
      .isFalse();
    Selenide.executeJavaScript("return window.scrollTo(0, document.body.scrollHeight);");
    assertThat(atBottom())
      .withFailMessage("oops, we shouldn't be at the bottom yet! " + printScrollParams())
      .isTrue();
  }

  private String printScrollParams() {
    return "\nwindow.scrollY=" + Selenide.executeJavaScript("return window.pageYOffset;") +
      "\nwindow.innerHeight=" + Selenide.executeJavaScript("return window.innerHeight;") +
      "\ndocument.body.scrollHeight=" + Selenide.executeJavaScript("return document.body.scrollHeight;");
  }
}
