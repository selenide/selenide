package integration.testng;

import com.codeborne.selenide.testng.annotations.Report;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

@Report
public class aSimpleNGTest extends BaseTestNGTest {
  @Test
  public void successfulMethod() {
    $("h1").shouldBe(visible).shouldHave(text("Selenide"));
  }
}
