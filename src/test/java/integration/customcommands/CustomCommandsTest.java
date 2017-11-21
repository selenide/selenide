package integration.customcommands;

import com.codeborne.selenide.commands.Commands;
import integration.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static integration.customcommands.MyFramework.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class CustomCommandsTest extends IntegrationTest {
  @Before
  public void setUpFramework() {
    MyFramework.setUp();
    tripleClickCounter.set(0);
    quadrupleClickCounter.set(0);
  }
  
  @Test
  public void userCanAddAnyCustomCommandsToSelenide() {
    assumeFalse(isHtmlUnit());
    $_("#valid-image").tripleClick().tripleClick().tripleClick().click();
    $_("#invalid-image").tripleClick().quadrupleClick();

    assertTrue("Can also use standard Selenium methods", $_("#valid-image img").isDisplayed());
    $_("#valid-image img").shouldBe(visible);
    
    assertEquals(4, tripleClickCounter.get());
    assertEquals(1, quadrupleClickCounter.get());
  }

  @Before
  public void openTestPage() {
    openFile("page_with_images.html");
  }

  @After
  public void resetSelenideDefaultCommands() {
    Commands.getInstance().resetDefaults();
  }
}
