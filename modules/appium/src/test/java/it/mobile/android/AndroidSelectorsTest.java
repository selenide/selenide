package it.mobile.android;

import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.appium.AppiumSelectors.byAttribute;
import static com.codeborne.selenide.appium.AppiumSelectors.byClassNameAndIndex;
import static com.codeborne.selenide.appium.AppiumSelectors.byContentDescription;
import static com.codeborne.selenide.appium.AppiumSelectors.byTagAndAttribute;
import static com.codeborne.selenide.appium.AppiumSelectors.byTagAndContentDescription;
import static com.codeborne.selenide.appium.AppiumSelectors.byTagAndText;
import static com.codeborne.selenide.appium.AppiumSelectors.byText;
import static com.codeborne.selenide.appium.AppiumSelectors.withAttribute;
import static com.codeborne.selenide.appium.AppiumSelectors.withContentDescription;
import static com.codeborne.selenide.appium.AppiumSelectors.withTagAndAttribute;
import static com.codeborne.selenide.appium.AppiumSelectors.withTagAndContentDescription;
import static com.codeborne.selenide.appium.AppiumSelectors.withTagAndText;
import static com.codeborne.selenide.appium.AppiumSelectors.withText;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static com.codeborne.selenide.appium.SelenideAppium.back;
import static com.codeborne.selenide.appium.selector.CombinedBy.android;

class AndroidSelectorsTest extends BaseApiDemosTest {

  private static final String VIEWS = "Views";

  @BeforeEach
  void setUp() {
    closeWebDriver();
    SelenideAppium.launchApp();
  }

  @Test
  void appiumSelectorsInAndroidApp() {
    testViewsLink(byAttribute("content-desc", VIEWS));
    testViewsLink(byContentDescription(VIEWS));
    testViewsLink(byTagAndAttribute("*", "text", VIEWS));
    testViewsLink(byTagAndContentDescription("*", VIEWS));
    testViewsLink(byTagAndText("*", VIEWS));
    testViewsLink(byText(VIEWS));

    testGraphicsLink(withAttribute("text", "Graphi"));
    testGraphicsLink(withContentDescription("Graphi"));
    testGraphicsLink(withTagAndAttribute("*", "text", "Graphi"));
    testGraphicsLink(withTagAndContentDescription("*", "Graphi"));
    testGraphicsLink(withTagAndText("android.widget.TextView", "Graphi"));
    testGraphicsLink(android(withText("Graphi")).ios(By.xpath("/")));
    testGraphicsLink(android(byText("Graphics")).ios(By.xpath("/")));
    testGraphicsLink(byClassNameAndIndex("android.widget.TextView", 7));
  }

  private void testViewsLink(By selector) {
    $(selector).click();
    $(byText("Auto Complete")).should(appear);
    back();
  }

  private void testGraphicsLink(By selector) {
    $(selector).click();
    $(byText("AlphaBitmap")).should(appear);
    back();
  }
}
