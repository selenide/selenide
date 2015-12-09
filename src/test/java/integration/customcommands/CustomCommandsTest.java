package integration.customcommands;

import com.codeborne.selenide.commands.Commands;
import integration.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static integration.customcommands.MyFramework.$_;
import static integration.customcommands.MyFramework.quadrupleClickCounter;
import static integration.customcommands.MyFramework.tripleClickCounter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    
    assertTrue("Can also use standard Selenide methods", $_("#valid-image img").isImage());
    assertFalse("Can also use standard Selenide methods", $_("#invalid-image img").isImage());
    
    assertEquals(4, tripleClickCounter.get());
    assertEquals(1, quadrupleClickCounter.get());
  }

  @Before
  public void openTestPage() {
    openFile("page_with_images.html");
  }

  @After
  public void resetSelenideDefaultCommands() {
    Commands.collection.resetDefaults();
  }
}
