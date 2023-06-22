package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.testng.SoftAsserts;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

public class TestNgSoftTest extends BaseTest {

  private final WebDriverListener listener1 = new WebDriverListener() {
    @Override
    public void beforeAnyCall(Object target, Method method, Object[] args) {
    }
  };
  private final AbstractWebDriverEventListener listener2 = new AbstractWebDriverEventListener() {
  };

  @Override
  @BeforeSuite
  final void setupAsserts() {
    Configuration.assertionMode = SOFT;
    SoftAsserts.fullStacktraces = false;
    closeWebDriver();
    WebDriverRunner.addListener(listener1);
    WebDriverRunner.addListener(listener2);
  }

  @BeforeMethod
  public void setUp() {
    open("/page_with_selects_without_jquery.html?browser=" + Configuration.browser);
  }

  @AfterClass
  public void afterClass() {
    WebDriverRunner.removeListener(listener1);
    WebDriverRunner.removeListener(listener2);
    closeWebDriver();
  }

  @Test
  public void header() {
    $("h1").shouldHave(text("Page with selects"));
  }

  @Test
  public void textArea() {
    $("#empty-text-area").val("text for textarea");
    $("#empty-text-area").shouldHave(value("text for textarea"));
  }
}
