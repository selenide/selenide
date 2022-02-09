package integration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.openqa.selenium.WindowType.TAB;

public class FirefoxCdpTest {
  public static void main(String[] args) {
    WebDriverManager.firefoxdriver().setup();
    FirefoxDriver driver = new FirefoxDriver();
//    driver.navigate().to("about:blank");
//    assert driver.getWindowHandles().size() == 1;
//    driver.switchTo().newWindow(TAB);
//    assert driver.getWindowHandles().size() == 2;
    driver.close();
//    assert driver.getWindowHandles().size() == 1;
//    driver.quit();
  }
}
