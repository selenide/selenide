package it.mobile.android;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.appium.SelenideAppiumCollection;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.appium.AppiumScrollOptions.up;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static com.codeborne.selenide.appium.SelenideAppium.$$;
import static com.codeborne.selenide.appium.SelenideAppium.$x;
import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginTestWithCollectionsTest extends BaseSwagLabsAndroidTest {
  @BeforeEach
  final void openLoginScreen() {
    SelenideAppium.openAndroidDeepLink("mydemoapprn://login", "com.saucelabs.mydemoapp.rn");
  }

  @Test
  public void loginTestWithPageObject() {
    LoginPageWithCollections loginPage = page();
    loginPage.inputFields.shouldHave(size(2)).get(0).setValue("bob@example.com");
    loginPage.inputFields.get(1).scroll(up());
    loginPage.inputFields.get(1).setValue("wrongpassword");
    loginPage.loginButton.click();
    loginPage.errorMessage.shouldHave(text("Provided credentials do not match any user in this service."));
  }

  @Test
  public void pageObjectWithSelenideCollection() {
    LoginPageWithSelenideCollection loginPage = page();
    loginPage.selenideElements.shouldHave(size(2));
    loginPage.selenideElements.first().setValue("bob@example.com");
    loginPage.selenideElements.last().setValue("secret");
  }

  @Test
  public void pageObjectWithSelenideElementList() {
    LoginPageWithSelenideElementList loginPage = page();
    for (long start = currentTimeMillis();
         currentTimeMillis() - start < timeout && loginPage.elements.size() != 2; ) {
      sleep(100);
    }
    assertThat(loginPage.elements).hasSize(2);
    loginPage.elements.get(0).setValue("bob@example.com");
    loginPage.elements.get(1).setValue("secret");
  }

  @Test
  public void pageObjectWithSelenideAppiumElementList() {
    LoginPageWithSelenideAppiumElementList loginPage = page();
    assertThat(loginPage.elements).hasSize(2);
    loginPage.elements.get(0).scroll(up()).setValue("bob@example.com");
    loginPage.elements.get(1).scroll(up()).setValue("secret");

    assertThat(loginPage.nonWebElements).isNull();
  }

  @Test
  public void pageObjectWithSelenideAppiumElementIterable() {
    LoginPageWithSelenideAppiumElementIterable loginPage = page();
    assertThat(loginPage.elements).hasSize(2);
    Iterator<SelenideAppiumElement> iterator = loginPage.elements.iterator();
    iterator.next().scroll(up()).setValue("bob@example.com");
    iterator.next().scroll(up()).setValue("secret");
  }

  @Test
  public void loginTestWithoutPageObject() {
    SelenideAppiumCollection inputFields = $$(By.xpath("//android.widget.EditText")).as("Input fields");
    inputFields.shouldHave(size(2)).get(0).setValue("bob@example.com");
    inputFields.get(1).scroll(up());
    inputFields.get(1).setValue("wrongpassword");

    SelenideAppiumElement loginButton = $(AppiumBy.accessibilityId("Login button")).as("Login button");
    loginButton.click();

    SelenideAppiumElement errorMessage = $x("//*[@content-desc='generic-error-message']/android.widget.TextView").as("Error message");
    errorMessage.shouldHave(text("Provided credentials do not match any user in this service."));
  }
}

class LoginPageWithCollections {
  @AndroidFindBy(xpath = "//android.widget.EditText")
  SelenideAppiumCollection inputFields;

  @AndroidFindBy(accessibility = "Login button")
  WebElement loginButton;

  @AndroidFindBy(xpath = "//*[@content-desc='generic-error-message']/android.widget.TextView")
  SelenideAppiumElement errorMessage;
}

class LoginPageWithSelenideCollection {
  @AndroidFindBy(xpath = "//android.widget.EditText")
  ElementsCollection selenideElements;
}

class LoginPageWithSelenideElementList {
  @AndroidFindBy(xpath = "//android.widget.EditText")
  List<SelenideElement> elements;
}

class LoginPageWithSelenideAppiumElementList {
  @AndroidFindBy(xpath = "//android.widget.EditText")
  List<SelenideAppiumElement> elements;

  @AndroidFindBy(xpath = "//android.widget.EditText")
  List<WebDriver> nonWebElements;
}

class LoginPageWithSelenideAppiumElementIterable {
  @AndroidFindBy(xpath = "//android.widget.EditText")
  Iterable<SelenideAppiumElement> elements;
}
