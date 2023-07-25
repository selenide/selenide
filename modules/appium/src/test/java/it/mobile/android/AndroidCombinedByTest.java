package it.mobile.android;

import com.codeborne.selenide.appium.selector.CombinedBy;
import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static com.codeborne.selenide.appium.SelenideAppium.openAndroidDeepLink;
import static java.time.Duration.ofSeconds;

class AndroidCombinedByTest extends BaseSwagLabsAndroidTest {


  @BeforeEach
  void openLoginScreen() {
    openAndroidDeepLink("mydemoapprn://login", "com.saucelabs.mydemoapp.rn");
  }

  @Test
  void combinedByAndroid() {

    int index = 1;
    CombinedBy username = CombinedBy
      .android(AppiumBy.xpath("(//android.widget.EditText)[" + index + "]"))
      .ios(AppiumBy.xpath("(//XCUIElementTypeTextField)[" + index + "]"));
    //selenide will choose appropriate locator at the runtime
    $(username).shouldBe(visible, ofSeconds(10));
  }
}
