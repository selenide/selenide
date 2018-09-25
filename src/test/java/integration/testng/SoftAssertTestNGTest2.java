package integration.testng;

import org.testng.annotations.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;

public class SoftAssertTestNGTest2 extends AbstractSoftAssertTestNGTest {
  @Test
  public void userCanUseSoftAssertWithTestNG2() {
    $("#radioButtons input").shouldHave(value("777"));
    $("#xxx").shouldBe(visible);
    $$("#radioButtons input").shouldHave(size(888));
    $("#radioButtons").$$("input").shouldHave(size(999));
    $("#radioButtons select").click();
  }

  @Test
  public void successfulTest2() {
  }
}
