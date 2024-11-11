package it.mobile.android;

import com.codeborne.selenide.appium.selector.CombinedBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static com.codeborne.selenide.appium.SelenideAppium.openAndroidDeepLink;
import static java.time.Duration.ofSeconds;

class AndroidCombinedByTest extends BaseSwagLabsAndroidTest {
  @BeforeEach
  final void openLoginScreen() {
    openAndroidDeepLink("mydemoapprn://login", "com.saucelabs.mydemoapp.rn");
  }

  @Test
  void combinedByAndroid() {
    int index = 1;
    CombinedBy username = CombinedBy
      .android(By.xpath("(//android.widget.EditText)[" + index + "]"))
      .ios(By.xpath("(//XCUIElementTypeTextField)[" + index + "]"));
    //selenide will choose appropriate locator at the runtime
    $(username).shouldBe(visible, ofSeconds(10));
  }
}
