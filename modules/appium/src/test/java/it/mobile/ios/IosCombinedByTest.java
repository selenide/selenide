package it.mobile.ios;

import com.codeborne.selenide.appium.selector.CombinedBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.appium.AppiumSelectors.withAttribute;
import static com.codeborne.selenide.appium.SelenideAppium.openIOSDeepLink;
import static java.time.Duration.ofSeconds;

class IosCombinedByTest extends BaseSwagLabsAppIosTest {

  private final CombinedBy username = CombinedBy
    .android(By.xpath("(//android.widget.EditText)[1]"))
    .ios(withAttribute("name", "Username input field"));

  @BeforeEach
  final void openLoginScreen() {
    openIOSDeepLink("mydemoapprn://login");
  }

  @Test
  void combinedByIos() {
    //selenide will choose appropriate locator at the runtime
    $$(username).get(0).shouldBe(visible, ofSeconds(10));
  }
}
