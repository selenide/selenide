package it.mobile.ios;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.appium.SelenideAppium.$;

public class LoginPage {
  private final By loginField = By.name("Username input field");
  private final By passwordField = By.name("Password input field");
  private final By loginButton = By.name("Login button");
  private final By errorMessage = By.name("generic-error-message");

  public LoginPage setLoginName(String name) {
    SelenideElement loginFieldElement = $(loginField);
    loginFieldElement.setValue(name);
    return this;
  }

  public LoginPage setPassword(String number) {
    $(passwordField).setValue(number);
    return this;
  }

  public LoginPage pressLogin() {
    $(loginButton).click();
    return this;
  }

  public LoginPage verifyWrongCredentials() {
    $(errorMessage).shouldBe(visible);
    $(errorMessage).shouldHave(text("Provided credentials do not match any user in this service."));
    return this;
  }
}
