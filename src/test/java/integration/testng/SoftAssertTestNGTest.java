package integration.testng;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.testng.SoftAsserts;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.AssertionMode.SOFT;
import static com.codeborne.selenide.Configuration.AssertionMode.STRICT;
import static com.codeborne.selenide.Selenide.*;

@Listeners(SoftAsserts.class)
public class SoftAssertTestNGTest {
  @BeforeMethod
  public void switchToSoftAssertionsMode() {
    open("http://google.com/ncr");
    Configuration.assertionMode = SOFT;
    Configuration.timeout = 0;
  }

  @AfterMethod
  public void resetDefaultProperties() {
    Configuration.assertionMode = STRICT;
    Configuration.timeout = 4000;
  }

  @Test//(enabled = false)
  public void userCanUseSoftAssertWithTestNG() {
    $("#radioButtons input").shouldHave(value("777"));
    $("#xxx").shouldBe(visible);
    $$("#radioButtons input").shouldHave(size(888));
    $("#radioButtons").$$("input").shouldHave(size(999));
    $("#radioButtons select").click();
  }

  @Test
  public void successfulTest() {
  }

  @Test//(enabled = false)
  public void userCanUseSoftAssert2() {
    $("#radioButtons input").shouldHave(value("777"));
    $("#xxx").shouldBe(visible);
    $$("#radioButtons input").shouldHave(size(888));
    $("#radioButtons").$$("input").shouldHave(size(999));
    $("#radioButtons select").click();
  }

  @Test//(enabled = false)
  public void userCanUseSoftAssert3() {
    $("#radioButtons input").shouldHave(value("777"));
    $("#xxx").shouldBe(visible);
    $$("#radioButtons input").shouldHave(size(888));
    $("#radioButtons").$$("input").shouldHave(size(999));
    $("#radioButtons select").click();
  }
}
