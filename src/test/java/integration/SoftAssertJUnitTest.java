package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit.SoftAsserts;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.AssertionMode.SOFT;
import static com.codeborne.selenide.Configuration.AssertionMode.STRICT;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

@Ignore
public class SoftAssertJUnitTest extends LegacyIntegrationTest {
  @Rule
  public SoftAsserts softAsserts = new SoftAsserts();

  @Before
  public void switchToSoftAssertionsMode() {
    open("/page_with_selects_without_jquery.html");
    Configuration.assertionMode = SOFT;
    Configuration.timeout = 0;
  }

  @After
  public void resetDefaultProperties() {
    Configuration.assertionMode = STRICT;
    Configuration.timeout = 4000;
  }

  @Test
  public void userCanUseSoftAssertWithJUnit() {
    $("#radioButtons input").shouldHave(value("777"));
    $("#xxx").shouldBe(visible);
    $$("#radioButtons input").shouldHave(size(888));
    $("#radioButtons").$$("input").shouldHave(size(999));
    $("#xxx").find("input").shouldBe(visible);
    $("#xxx").$$("input").shouldHave(size(999));
    $("#radioButtons select").click();
  }
}
