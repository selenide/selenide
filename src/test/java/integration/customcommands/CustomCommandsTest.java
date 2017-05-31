package integration.customcommands;

import com.codeborne.selenide.commands.Commands;
import integration.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.visible;
import static integration.customcommands.MyFramework.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomCommandsTest extends IntegrationTest {
  @Before
  public void setUpFramework() {
    MyFramework.setUp();
    tripleClickCounter.set(0);
    quadrupleClickCounter.set(0);
  }
  
  @Test
  public void userCanAddAnyCustomCommandsToSelenide() {
    $_("#valid-image").tripleClick().tripleClick().tripleClick().click();
    $_("#invalid-image").tripleClick().quadrupleClick();
    
    assertTrue("Can also use standard Selenium methods", $_("#valid-image img").isDisplayed());
    assertTrue("Can also use standard Selenide methods", $_("#invalid-image img").is(visible));
    
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
