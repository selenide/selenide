package integration.testng;

import com.codeborne.selenide.testng.TextReport;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;

@Listeners(TextReport.class)
public class GoogleTestNGTest extends BaseTestNGTest {
  @BeforeMethod
  public void setUp() {
    TextReport.onSucceededTest = false;
    TextReport.onFailedTest = true;
    driver.open("http://google.com/ncr");
  }

  @AfterMethod
  public void tearDown() {
    driver.close();
  }

  @Test(enabled = false)
  public void failingMethod() {
    $(By.name("q")).shouldBe(visible, enabled);
    $("#missing-button").click();
  }

  @Test
  public void successfulMethod() {
    $(By.name("q")).setValue("selenide").pressEnter();
    $$("#ires .g").shouldHave(size(10));
  }
}
