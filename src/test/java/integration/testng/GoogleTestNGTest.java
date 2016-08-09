package integration.testng;

import com.codeborne.selenide.testng.TextReport;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@Listeners(TextReport.class)
public class GoogleTestNGTest {
  @BeforeMethod
  public void setUp() {
    open("http://google.com/ncr");
  }

  @Test(enabled = false)
  public void failingMethod() {
    $(By.name("q")).shouldBe(visible, enabled);
    $("#missing-button").click();
  }

  @Test(enabled = false)
  public void successfulMethod() {
    $(By.name("q")).setValue("selenide").pressEnter();
    $$("#ires .g").shouldHave(size(10));
  }
}
