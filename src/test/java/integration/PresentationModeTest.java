package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class PresentationModeTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    Configuration.presentationMode.active = true;
    openFile("page_for_presentation_mode.html");
  }

  @Test
  public void markTest() {
    Configuration.presentationMode.flashElements = false;
    Configuration.presentationMode.markElements = true;
    ElementsCollection divs = $$("div.rect");
    for (SelenideElement div: divs) {
      div.click();
    }

    ElementsCollection markers = $$("div#selenideMarker");
    markers.shouldHaveSize(5);
    for (SelenideElement marker: markers) {
      marker.shouldBe(visible);
    }
  }

  @Test
  public void flashTest() {
    Configuration.presentationMode.flashElements = true;
    Configuration.presentationMode.markElements = false;
    ElementsCollection divs = $$("div.rect");
    for (SelenideElement div: divs) {
      div.click();
    }

    ElementsCollection markers = $$("div#selenideFlasher");
    markers.shouldHaveSize(0);
  }

  @AfterClass
  public static void tearDown() {
    close();
  }
}
