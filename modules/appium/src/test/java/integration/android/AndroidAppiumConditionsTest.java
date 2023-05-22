package integration.android;

import com.codeborne.selenide.appium.AppiumCollectionCondition;
import com.codeborne.selenide.appium.AppiumCondition;
import com.codeborne.selenide.appium.SelenideAppium;
import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;

class AndroidAppiumConditionsTest extends BaseApiDemosTest {

  @BeforeEach
  void setUp() {
    closeWebDriver();
    SelenideAppium.launchApp();
  }

  @Test
  void appiumCollectionConditionAttribute() {

    List<String> expectedList =
      Arrays.asList("API Demos", "KeyEventText", "Linkify", "LogTextBox", "Marquee", "Unicode");

    $(By.xpath(".//*[@text='Text']")).click();
    $$(AppiumBy.xpath("//android.widget.TextView"))
      .shouldHave(AppiumCollectionCondition.exactAttributes("text", "name", expectedList));
  }

  @Test
  void appiumConditionAttribute() {

    $(AppiumBy.accessibilityId("Accessibility"))
      .shouldHave(AppiumCondition.attributeWithValue("content-desc", "name", "Accessibility"));
  }
}
