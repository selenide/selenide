package integration;

import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ChromeTest {

  public static void main(String[] args) {
    WebDriverManager.chromedriver().setup();

    ChromeDriver driver = new ChromeDriver();
    driver.navigate().to("https://google.com");
    System.out.println("Quitting...");
    driver.quit();
    System.out.println("Quited.");
  }
}
