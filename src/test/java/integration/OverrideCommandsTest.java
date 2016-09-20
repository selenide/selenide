package integration;

import com.codeborne.selenide.commands.Click;
import com.codeborne.selenide.commands.Commands;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.concurrent.atomic.AtomicInteger;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;

public class OverrideCommandsTest extends IntegrationTest {
  
  private AtomicInteger clickCounter = new AtomicInteger();

  @Before
  public void openTestPageWithImages() {
    openFile("page_with_images.html");
  }
  
  @After
  public void tearDown() {
    Commands.getInstance().resetDefaults();
  }

  @Test
  public void userCanOverrideAnyCommand() {
    Commands.getInstance().add("click", new MyClick());
    $("#valid-image").click();
    $("#invalid-image").click();
    assertEquals(2, clickCounter.get());
  }

  private class MyClick extends Click {
    @Override
    protected void click(WebElement element) {
      super.click(element);
      clickCounter.incrementAndGet();
    }
  }
}
