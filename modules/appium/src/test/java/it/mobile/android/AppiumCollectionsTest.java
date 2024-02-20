package it.mobile.android;

import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.appium.SelenideAppiumCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.appium.AppiumScrollOptions.down;
import static com.codeborne.selenide.appium.AppiumScrollOptions.up;
import static com.codeborne.selenide.appium.SelenideAppium.$$;
import static org.assertj.core.api.Assertions.assertThat;

public class AppiumCollectionsTest extends BaseSwagLabsAndroidTest {
  @BeforeEach
  final void openLoginScreen() {
    SelenideAppium.openAndroidDeepLink("mydemoapprn://login", "com.saucelabs.mydemoapp.rn");
  }

  @Test
  void appiumCollectionMethods() {
    SelenideAppiumCollection inputFields = $$(By.xpath("//android.widget.EditText"));
    assertThat(inputFields.first(1))
      .isInstanceOf(SelenideAppiumCollection.class)
      .hasSize(1);

    assertThat(inputFields.last(2))
      .isInstanceOf(SelenideAppiumCollection.class)
      .hasSize(2);

    inputFields.first(1).shouldHave(size(1));
    inputFields.last(2).shouldHave(size(2));

    inputFields.first(1).get(0).scroll(up()).shouldHave(attribute("password", "false"));
    inputFields.last(1).get(0).scroll(down()).shouldHave(attribute("password", "true"));
  }
}
