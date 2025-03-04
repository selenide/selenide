package it.mobile.android;

import com.codeborne.selenide.As;
import com.codeborne.selenide.Container;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.appium.SelenideAppiumCollection;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import it.mobile.android.LoginPageWithContainer.ErrorsWidget;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.appium.AppiumScrollOptions.up;
import static com.codeborne.selenide.appium.SelenideAppium.$;
import static com.codeborne.selenide.appium.SelenideAppium.$$;
import static com.codeborne.selenide.appium.SelenideAppium.$x;
import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.IterableUtil.sizeOf;

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
  public void pageObjectWithContainer() {
    LoginPageWithContainer loginPage = page();
    assertThat(loginPage.inputFields).hasSize(2);
    loginPage.inputFields.get(0).sendKeys("bob@example.com");
    loginPage.inputFields.get(1).scroll(up());
    loginPage.inputFields.get(1).setValue("wrongpassword");
    loginPage.loginButton.click();
    assertThat(loginPage.errors).hasSize(1);
    loginPage.errors.get(0).message.shouldHave(text("Provided credentials do not match any user in this service."));
    loginPage.error.message.shouldHave(text("Provided credentials do not match any user in this service."));
    loginPage.error.self.shouldBe(visible);
  }

  @Test
  public void pageObjectWithErrors() {
    LoginPageWithContainer loginPage = page();
    loginPage.inputFields.get(0).sendKeys("bob@example.com");
    loginPage.inputFields.get(1).setValue("wrongpassword");
    loginPage.loginButton.click();

    LoginPageWithErrors page = page();
    assertThat(page.errors).hasSize(1);
    page.errors.get(0).message.shouldHave(text("Provided credentials do not match any user in this service."));
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
    waitUntilCollectionFullyLoaded(loginPage.elements, 2);
    assertThat(loginPage.elements).hasSize(2);
    loginPage.elements.get(0).setValue("bob@example.com");
    loginPage.elements.get(1).setValue("secret");
  }

  @Test
  public void pageObjectWithSelenideAppiumElementList() {
    LoginPageWithSelenideAppiumElementList loginPage = page();
    waitUntilCollectionFullyLoaded(loginPage.elements, 2);
    assertThat(loginPage.elements).hasSize(2);
    loginPage.elements.get(0).scroll(up()).setValue("bob@example.com");
    loginPage.elements.get(1).scroll(up()).setValue("secret");

    assertThat(loginPage.nonWebElements).isNull();
  }

  @Test
  public void pageObjectWithSelenideAppiumElementIterable() {
    LoginPageWithSelenideAppiumElementIterable loginPage = page();
    waitUntilCollectionFullyLoaded(loginPage.elements, 2);
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

  private static void waitUntilCollectionFullyLoaded(Iterable<?> collection, int expectedSize) {
    for (long start = currentTimeMillis();
         currentTimeMillis() - start < timeout && sizeOf(collection) != expectedSize; ) {
      sleep(100);
    }
  }
}

class LoginPageWithCollections {
  @As("inputs")
  @AndroidFindBy(xpath = "//android.widget.EditText")
  SelenideAppiumCollection inputFields;

  @As("Login button")
  @AndroidFindBy(accessibility = "Login button")
  WebElement loginButton;

  @As("Error message")
  @AndroidFindBy(xpath = "//*[@content-desc='generic-error-message']/android.widget.TextView")
  SelenideAppiumElement errorMessage;
}

class LoginPageWithContainer {
  @As("inputs")
  @AndroidFindBy(xpath = "//android.widget.EditText")
  List<SelenideAppiumElement> inputFields;

  @As("Login button")
  @AndroidFindBy(accessibility = "Login button")
  WebElement loginButton;

  @As("error")
  @AndroidFindBy(xpath = "//*[@content-desc='generic-error-message']")
  ErrorsWidget error;

  @As("errors")
  @AndroidFindBy(xpath = "//*[@content-desc='generic-error-message']")
  List<ErrorsWidget> errors;

  static class ErrorsWidget implements Container {
    @Self
    SelenideAppiumElement self;

    // @As("message")
    @AndroidFindBy(xpath = ".//android.widget.TextView")
    SelenideAppiumElement message;
  }
}
class LoginPageWithErrors {
  @As("errors")
  @AndroidFindBy(xpath = "//*[@content-desc='generic-error-message']")
  List<ErrorsWidget> errors;
}

class LoginPageWithSelenideCollection {
  @As("inputs")
  @AndroidFindBy(xpath = "//android.widget.EditText")
  ElementsCollection selenideElements;
}

class LoginPageWithSelenideElementList {
  @As("inputs")
  @AndroidFindBy(xpath = "//android.widget.EditText")
  List<SelenideElement> elements;
}

class LoginPageWithSelenideAppiumElementList {
  @As("inputs")
  @AndroidFindBy(xpath = "//android.widget.EditText")
  List<SelenideAppiumElement> elements;

  @AndroidFindBy(xpath = "//android.widget.EditText")
  @Nullable
  List<WebDriver> nonWebElements;
}

class LoginPageWithSelenideAppiumElementIterable {
  @As("inputs")
  @AndroidFindBy(xpath = "//android.widget.EditText")
  Iterable<SelenideAppiumElement> elements;
}
