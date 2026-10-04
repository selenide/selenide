package it.mobile.android;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.appium.SelenideAppiumCollection;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.AppiumScrollOptions.down;
import static com.codeborne.selenide.appium.AppiumScrollOptions.up;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static com.codeborne.selenide.appium.SelenideAppium.$$;
import static org.assertj.core.api.Assertions.assertThat;

public class AppiumCollectionsTest extends BaseSwagLabsAndroidTest {
  @BeforeEach
  final void openLoginScreen() {
    SelenideAppium.openAndroidDeepLink("mydemoapprn://login", "com.saucelabs.mydemoapp.rn");
  }

  @Test
  void appiumCollectionMethods() {
    SelenideAppiumCollection inputFields = $$(By.xpath("//android.widget.EditText")).shouldHave(size(2));

    assertThat(inputFields.first(1))
      .isInstanceOf(SelenideAppiumCollection.class)
      .hasSize(1);

    assertThat(inputFields.last(2))
      .isInstanceOf(SelenideAppiumCollection.class)
      .hasSize(2);

    inputFields.first(1).shouldHave(size(1));
    inputFields.last(2).shouldHave(size(2));

    assertThat(inputFields.first()).isInstanceOf(SelenideAppiumElement.class);
    assertThat(inputFields.last()).isInstanceOf(SelenideAppiumElement.class);

    inputFields.first(1).get(0).scroll(up()).shouldHave(attribute("password", "false"));
    inputFields.last(1).get(0).scroll(down()).shouldHave(attribute("password", "true"));

    SelenideAppiumElement field = inputFields.find(attribute("password", "true")).shouldBe(visible);
    assertThat(field).isInstanceOf(SelenideAppiumElement.class);
  }

  @Test
  void collectionMethodsChainedFromAParentElement() {
    SelenideAppiumElement loginForm = $(By.xpath("//android.view.ViewGroup[@content-desc='Login button']/.."));

    SelenideAppiumCollection viaFindAll = loginForm.findAll(By.xpath(".//android.widget.EditText")).shouldHave(size(2));
    assertThat(viaFindAll.first()).isInstanceOf(SelenideAppiumElement.class);

    SelenideAppiumCollection viaDollarDollar = loginForm.$$(By.xpath(".//android.widget.EditText")).shouldHave(size(2));
    assertThat(viaDollarDollar.get(0)).isInstanceOf(SelenideAppiumElement.class);

    SelenideAppiumCollection viaXpathShortcut = loginForm.$$x(".//android.widget.EditText").shouldHave(size(2));
    assertThat(viaXpathShortcut.last()).isInstanceOf(SelenideAppiumElement.class);
  }

  @Test
  void singleElementMethodsChainedFromAParentElement() {
    SelenideAppiumElement loginForm = $(By.xpath("//android.view.ViewGroup[@content-desc='Login button']/.."));

    // these calls only compile at all if find()/$()/$x() are typed as SelenideAppiumElement, not SelenideElement:
    loginForm.find(By.xpath(".//android.widget.EditText")).scroll(up()).shouldHave(attribute("password", "false"));
    loginForm.$(By.xpath(".//android.widget.EditText"), 1).scroll(down()).shouldHave(attribute("password", "true"));
    SelenideAppiumElement field = loginForm.$x(".//android.widget.EditText");
    assertThat(field).isInstanceOf(SelenideAppiumElement.class);
  }

  @Test
  void elementsProducedByIteratingACollection_canBeSafelyCastToSelenideAppiumElement() {
    SelenideAppiumCollection inputFields = $$(By.xpath("//android.widget.EditText")).shouldHave(size(2));

    for (SelenideElement element : inputFields) {
      SelenideAppiumElement appiumElement = (SelenideAppiumElement) element;
      assertThat(appiumElement).isInstanceOf(SelenideAppiumElement.class);
    }
  }
}
