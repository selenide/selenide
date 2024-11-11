package it.mobile.ios;

import com.codeborne.selenide.appium.SelenideAppium;
import org.junit.jupiter.api.Test;


public class LoginTest extends BaseSwagLabsAppIosTest {
  @Test
  void loginTest() {
    SelenideAppium.openIOSDeepLink("mydemoapprn://login");
    LoginPage page = new LoginPage();

    page
      .setLoginName("bob@example.com")
      .setPassword("hackerpassword")
      .pressLogin()
      .verifyWrongCredentials();

  }
}

