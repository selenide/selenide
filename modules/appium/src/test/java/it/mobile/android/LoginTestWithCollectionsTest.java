package it.mobile.android;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppium;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.appium.ScreenObject.screen;

public class LoginTestWithCollectionsTest extends BaseSwagLabsAndroidTest {
  @BeforeEach
  void openLoginScreen() {
    SelenideAppium.openAndroidDeepLink("mydemoapprn://login", "com.saucelabs.mydemoapp.rn");
  }

  @Test
  public void loginTestPageObject() {
    LoginPageWithCollections loginPage = screen(LoginPageWithCollections.class);
    loginPage.inputFields.shouldHave(size(2)).get(0).setValue("bob@example.com");
    loginPage.inputFields.get(1).setValue("wrongpassword");
    loginPage.loginButton.click();
    loginPage.errorMessage.shouldHave(text("Provided credentials do not match any user in this service."));
  }
}

class LoginPageWithCollections {
  @AndroidFindBy(xpath = "//android.widget.EditText")
  ElementsCollection inputFields;

  @AndroidFindBy(accessibility = "Login button")
  WebElement loginButton;

  @AndroidFindBy(xpath = "//*[@content-desc='generic-error-message']/android.widget.TextView")
  SelenideElement errorMessage;
}
