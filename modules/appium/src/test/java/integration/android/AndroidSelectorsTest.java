package integration.android;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.appium.AppiumSelectors.*;
import static com.codeborne.selenide.appium.SelenideAppium.back;

class AndroidSelectorsTest extends BaseApiDemosTest {

  private static final String VIEWS = "Views";
  private static final String GRAPHICS_PARTIAL_STRING = "Graphi";

  @Test
  void testAppiumSelectorsInAndroidApp() {
    $(byAttribute("content-desc", VIEWS)).click();
    back();
    $(byContentDescription(VIEWS)).click();
    back();
    $(byTagAndAttribute("*", "text", VIEWS)).click();
    back();
    $(byTagAndContentDescription("*", VIEWS)).click();
    back();
    $(byTagAndText("*", VIEWS)).click();
    back();
    $(byText(VIEWS)).click();
    back();
    $(withAttribute("text", GRAPHICS_PARTIAL_STRING)).click();
    back();
    $(withContentDescription(GRAPHICS_PARTIAL_STRING)).click();
    back();
    $(withTagAndAttribute("*", "text", GRAPHICS_PARTIAL_STRING)).click();
    back();
    $(withTagAndContentDescription("*", GRAPHICS_PARTIAL_STRING)).click();
    back();
    $(withTagAndText("android.widget.TextView", GRAPHICS_PARTIAL_STRING)).click();
    back();
    $(withText(GRAPHICS_PARTIAL_STRING))
      .shouldHave(text("Graphics"));
  }
}
