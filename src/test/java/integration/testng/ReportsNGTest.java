package integration.testng;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.testng.TextReport;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Listeners(TextReport.class)
public class ReportsNGTest extends BaseTestNGTest {
  @BeforeClass
  public void setUp() throws Exception {
    startServer();
  }

  @Test(expectedExceptions = ElementNotFound.class)
  public void failingMethod() {
    $("h22").shouldBe(visible).shouldHave(text("Selenide"));
  }

  @Test
  public void successfulMethod() {
    $("h1").shouldBe(visible, text("Selenide"));
  }

  @Test
  public void reportingCollections() {
    $$("h1").shouldHaveSize(1);
    $$("h2").shouldHaveSize(1);
    $("h1").shouldBe(visible);
  }
}
