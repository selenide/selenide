package integration.testng;

import com.codeborne.selenide.testng.annotations.Report;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Report
public class aSimpleNGTest extends BaseTestNGTest {
  @BeforeClass
  public void setUp() throws Exception {
    startServer();
  }

  @Test
  public void successfulMethod() {
    $("h1").shouldBe(visible).shouldHave(text("Selenide"));
  }
}
