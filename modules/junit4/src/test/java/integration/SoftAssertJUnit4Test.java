package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit.SoftAsserts;
import com.codeborne.selenide.junit.TextReport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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

  @Before
  public void setUp() {
    Configuration.assertionMode = SOFT;
    open("https://duckduckgo.com/");
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
    // assertThat("male").isEqualTo("female");
  }

  @AfterClass
  public static void afterAll() {
    Configuration.assertionMode = STRICT;
    $("#soft-assert-logout").shouldNot(exist);
    closeWebDriver();
  }
}
