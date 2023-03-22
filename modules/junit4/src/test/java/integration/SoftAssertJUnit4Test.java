package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit.SoftAsserts;
import com.codeborne.selenide.junit.TextReport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.Method;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.AssertionMode.STRICT;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

public class SoftAssertJUnit4Test extends IntegrationTest {
  @Rule @SuppressWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
  public final SoftAsserts softAsserts = new SoftAsserts();

  @Rule @SuppressWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
  public final TextReport textReport = new TextReport();

  private static final WebDriverListener listener1 = new WebDriverListener() {
    @Override
    public void beforeAnyCall(Object target, Method method, Object[] args) {
    }
  };
  private static final AbstractWebDriverEventListener listener2 = new AbstractWebDriverEventListener() {
  };

  @BeforeClass
  public static void beforeClass() {
    closeWebDriver();
    WebDriverRunner.addListener(listener1);
    WebDriverRunner.addListener(listener2);
  }

  @Before
  public void setUp() {
    Configuration.assertionMode = SOFT;
    open("about:blank");
    $("#soft-assert-login").shouldNot(exist);
  }

  @Test
  public void blah1() {
    $("#foo").shouldNot(exist);
  }

  @Test
  public void blah2() {
    $("#zoo").shouldNot(exist);

    // uncomment to trigger test failure
    // $("#foo").should(exist);
    // $("#bar").should(exist);
  }

  @After
  public void tearDown() {
    // uncomment to trigger test failure
    // assertEquals("male", "female");
  }

  @AfterClass
  public static void afterAll() {
    Configuration.assertionMode = STRICT;
    $("#soft-assert-logout").shouldNot(exist);

    WebDriverRunner.removeListener(listener1);
    WebDriverRunner.removeListener(listener2);
    closeWebDriver();
  }
}
