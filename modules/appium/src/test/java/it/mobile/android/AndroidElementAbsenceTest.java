package it.mobile.android;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static org.assertj.core.api.Assertions.assertThat;

class AndroidElementAbsenceTest extends BaseApiDemosTest {
  @Test
  void checkThatElementIsAbsent() {
    SelenideElement webElement = Selenide.$(By.xpath("Nope"));
    webElement.shouldNot(exist);

    SelenideAppiumElement mobileElement = $(webElement);
    mobileElement.shouldNot(exist);
  }

  @Test
  void exists() {
    assertThat(Selenide.$(By.id("Not_existing")).exists()).isFalse();

    assertThat(SelenideAppium.$(By.id("Not_existing")).exists()).isFalse();

    assertThat($(Selenide.$(By.id("Not_existing"))).exists()).isFalse();
  }
}
