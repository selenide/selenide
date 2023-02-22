package integration;

import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class FirefoxTest {

  public static void main(String[] args) {
    WebDriverManager.firefoxdriver().setup();

    FirefoxDriver driver = new FirefoxDriver();
    driver.navigate().to("https://google.com");
    driver.quit();
  }
}
