package integration;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;


class issue974Test extends ITest {
  private SelenideDriver driver;

  @BeforeEach
  void setup() {
    String driver1URL = getBaseUrl();
    driver = new SelenideDriver(new SelenideConfig().baseUrl(driver1URL));
  }

  @Test
  void frameValueSet1() {

    driver.open("/issue.html");
    driver.switchTo().frame("fid");
    driver.$(By.id("b")).setValue("test_b");

  }

  @Test
  void frameValueSet2() {
    driver.open("/issue.html");
    driver.switchTo().frame("fid");
    driver.$(By.id("b")).setValue("test_b");

    driver.switchTo().parentFrame();
    driver.$(By.id("a")).setValue("test_a");
  }
}
