package integration.android;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.appium.ScreenObject.screen;
import static io.appium.java_client.AppiumBy.accessibilityId;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppium;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

public class LoginTest extends BaseSwagLabsAndroidTest {
  @BeforeEach
  void openLoginScreen() {
    closeWebDriver();
    SelenideAppium.openAndroidDeepLink("mydemoapprn://login", "com.saucelabs.mydemoapp.rn");
  }

  @Test
  void loginTest() {
    $(accessibilityId("Username input field")).setValue("bob@example.com");
    $(accessibilityId("Password input field")).setValue("10203040");
    $(accessibilityId("Login button")).click();
    $(accessibilityId("checkout address screen")).shouldBe(visible);
  }

  @Test
  public void loginTestPageObject() {
    LoginPage loginPage = screen(LoginPage.class);
    loginPage.login.setValue("bob@example.com");
    loginPage.password.setValue("wrongpassword");
    loginPage.loginButton.click();
    loginPage.errorMessage.shouldHave(text("Provided credentials do not match any user in this service."));
  }
}

class LoginPage {
  @AndroidFindBy(accessibility = "Username input field")
  SelenideElement login;

  @AndroidFindBy(accessibility = "Password input field")
  SelenideElement password;

  @AndroidFindBy(accessibility = "Login button")
  WebElement loginButton;

  @AndroidFindBy(xpath = "//*[@content-desc='generic-error-message']/android.widget.TextView")
  SelenideElement errorMessage;
}
