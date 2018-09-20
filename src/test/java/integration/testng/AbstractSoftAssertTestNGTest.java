package integration.testng;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.testng.SoftAsserts;
import org.assertj.core.api.WithAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import static com.codeborne.selenide.AssertionMode.SOFT;
import static com.codeborne.selenide.AssertionMode.STRICT;
import static com.codeborne.selenide.Selenide.open;

@Listeners(SoftAsserts.class)
public abstract class AbstractSoftAssertTestNGTest implements WithAssertions {
  @BeforeMethod
  public void switchToSoftAssertionsMode() {
    open("http://google.com/ncr");
    Configuration.assertionMode = SOFT;
    Configuration.timeout = 0;
  }

  @AfterMethod
  public void resetDefaultProperties() {
    Configuration.assertionMode = STRICT;
    Configuration.timeout = 4000;
  }
}
