package integration;

import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

class OpacityTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_opaque_elements.html");
  }

  @Test
  void opaqueElementIsInvisibleButClickable() {
    $("#inv-link").should(exist)
      .shouldNotBe(visible);
    $("#inv-link").click();
    $("#inv-link").doubleClick();
    $("#inv-link").contextClick();
  }

  @Test
  void almostOpaqueElementIsVisibleAndClickable() {
    $("#link").shouldBe(visible);
    $("#link").click();
    $("#link").doubleClick();
    $("#link").contextClick();
  }

  @Test
  void opaqueElementIsInvisibleButGetsInput() {
    $("#inv-input").should(exist)
      .shouldNotBe(visible);
    $("#inv-input").setValue("abc");
    $("#inv-input").clear();
    $("#inv-input").pressEnter();
    $("#inv-input").pressEscape();
    $("#inv-input").pressTab();
  }

  @Test
  void almostOpaqueElementIsVisibleAndGetsInput() {
    $("#input").shouldBe(visible);
    $("#input").setValue("abc");
    $("#input").clear();
    $("#input").pressEnter();
    $("#input").pressEscape();
    $("#input").pressTab();
  }


}
