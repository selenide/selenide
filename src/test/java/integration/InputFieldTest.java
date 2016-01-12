package integration;

import com.codeborne.selenide.*;
import org.junit.*;
import org.openqa.selenium.*;

import static com.codeborne.selenide.Selenide.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by vinogradov on 11.01.16.
 */

public class InputFieldTest extends IntegrationTest {


  @Test
  public void selenideClearTest(){
    open("/html5_input.html");
    SelenideElement input=$(By.id("id1"));
    input.toWebElement().clear();
    input.toWebElement().sendKeys(",.123");
    input.toWebElement().clear();
    input.toWebElement().sendKeys("456");
    Assert.assertThat(input.getAttribute("value"),is(equalTo("456")));
  }

  @Test
  public void selenideClearTest2(){
    open("/html5_input.html");
    WebElement input=$(By.id("id1")).toWebElement();
    input.clear();
    input.sendKeys(",.123");
    input.clear();
    input.sendKeys("456");
    Assert.assertThat(input.getAttribute("value"),is(equalTo("456")));
  }

  @Test
  public void seleniumClearTest(){
    open("/html5_input.html");
    WebElement input=WebDriverRunner.getWebDriver().findElement(By.id("id1"));
    input.clear();
    input.sendKeys(",.123");
    input.clear();
    input.sendKeys("456");
    Assert.assertThat(input.getAttribute("value"),is(equalTo("456")));
  }
}
