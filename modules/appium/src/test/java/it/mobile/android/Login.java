package it.mobile.android;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.TypeOptions;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static io.appium.java_client.AppiumBy.accessibilityId;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

class SauceLabLoginTest extends BaseSwagLabsAndroidTest {

  @BeforeEach
  void openLoginScreen() {
    closeWebDriver();
    SelenideAppium.openAndroidDeepLink("mydemoapprn://login", "com.saucelabs.mydemoapp.rn");
  }

  @Test
  void loginTest() {
    $(accessibilityId("Username input field")).shouldBe(visible, ofSeconds(10)).setValue("bob@example.com");
    $(accessibilityId("Password input field")).setValue("10203040");
    $(accessibilityId("Login button")).click();
    $(accessibilityId("checkout address screen")).shouldBe(visible, ofSeconds(10));
  }

  @Test
  void loginTestPageObject() {
    LoginPage loginPage = page();
    loginPage.login.shouldBe(visible, ofSeconds(10)).setValue("bob@example.com");
    loginPage.password.setValue("wrongpassword");
    loginPage.loginButton.click();
    loginPage.errorMessage
      .shouldBe(visible, ofSeconds(10))
      .shouldHave(text("Provided credentials do not match any user in this service."));
  }

  @Test
  void canTypeTextSlowly() {
    LoginPage loginPage = page();
    loginPage.login.type(TypeOptions.text("bob@example.com").withDelay(ofMillis(10)));
    loginPage.password.sendKeys("10203040");
    loginPage.loginButton.click();

    CheckoutPage nextPage = page();
    nextPage.title.shouldBe(visible, ofSeconds(10));
  }
}

class LoginPage {
  @AndroidFindBy(accessibility = "Username input field")
  SelenideAppiumElement login;

  @AndroidFindBy(accessibility = "Password input field")
  SelenideElement password;

  @AndroidFindBy(accessibility = "Login button")
  WebElement loginButton;

  @AndroidFindBy(xpath = "//*[@content-desc='generic-error-message']/android.widget.TextView")
  SelenideElement errorMessage;
}

class CheckoutPage {
  @AndroidFindBy(accessibility = "checkout address screen")
  SelenideAppiumElement title;
}
