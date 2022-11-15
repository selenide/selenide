package integration.ios;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.appium.AppiumSelectors.*;

class IosSelectorsTest extends BaseIOSTest {

  @Test
  void testAppiumSelectorsInIosApp() {
    $(byTagAndName("*", "IntegerA")).setValue("2");
    $(byName("IntegerB")).setValue("4");
    $(withName("ComputeSum")).click();
    $(withTagAndName("*", "Answ")).shouldHave(text("6"));
  }
}
