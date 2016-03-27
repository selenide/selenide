package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class CustomWebDriverProviderTest extends IntegrationTest {
  private String originalWebdriver;
  
  @Before
  public void setUp() {
    assumeTrue(isFirefox());
    
    close();
    originalWebdriver = Configuration.browser;
  }

  @After
  public void tearDown() {
    close();
    Configuration.browser = originalWebdriver;
  }

  @Test
  public void userCanImplementAnyCustomWebdriverProvider() {
    Configuration.browser = CustomWebDriverProvider.class.getName();
    
    openFile("autocomplete.html");
    
    assertTrue(WebDriverRunner.getWebDriver() instanceof CustomFirefoxDriver);
  }

  private static class CustomFirefoxDriver extends FirefoxDriver {
  }
  
  private static class CustomWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
      return new CustomFirefoxDriver();
    }
  }
}
