package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

final class TransparencyAndOtherNonStandardVisibilityTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_transparent_elements.html");
  }

  @Test
  void nosizeElementIsVisibleAndClickable() {
    $("#no-size-link").should(exist)
      .shouldBe(visible);
    $("#no-size-link").click();
    $("#no-size-link").doubleClick();
    $("#no-size-link").contextClick();
  }

  @Test
  void transparentElementIsInvisibleButClickable() {
    $("#inv-link").should(exist).shouldNotBe(visible);
    $("#inv-link").click();
    $("#inv-link").doubleClick();
    $("#inv-link").contextClick();
  }

  @Test
  void almostTransparentElementIsVisibleAndClickable() {
    $("#link").shouldBe(visible);
    $("#link").click();
    $("#link").doubleClick();
    $("#link").contextClick();
  }

  @Test
  void transparentElementIsInvisibleButGetsInput() {
    $("#inv-input").should(exist).shouldNotBe(visible);
    $("#inv-input").setValue("abc");
    $("#inv-input").clear();
    $("#inv-input").pressEnter();
    $("#inv-input").pressEscape();
    $("#inv-input").pressTab();
  }

  @Test
  void almostTransparentElementIsVisibleAndGetsInput() {
    $("#input").shouldBe(visible);
    $("#input").setValue("abc");
    $("#input").clear();
    $("#input").pressEnter();
    $("#input").pressEscape();
    $("#input").pressTab();
  }
}
