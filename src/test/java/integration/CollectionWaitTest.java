package integration;

import com.codeborne.selenide.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class CollectionWaitTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("collection_with_delays.html");
  }

  @Test
  public void waitsUntilNthElementAppears() {
    $$("#collection li").get(5).shouldBe(visible);
    $$("#collection li").get(33).shouldBe(visible);
    $$("#collection li").get(49).shouldBe(visible);

    $$("#collection li").first().shouldBe(visible).shouldHave(text("Element #0"));
    $$("#collection li").last().shouldBe(visible).shouldHave(text("Element #49"));
  }

  @Test
  public void reproduceStaleElementException_priorToSelenide33() {
    List<SelenideElement> elements = new ArrayList<>();
    for (SelenideElement selenideElement : $$("h1")) {
      elements.add(selenideElement);
    }
    executeJavaScript("window.location.reload();");

    elements.get(0).shouldBe(visible).shouldHave(text("Elements will appear soon"));
  }


  @Test(expected = AssertionError.class)
  public void failsIfWrongSize() {
    $$("#collection li").shouldHave(CollectionCondition.size(4));
  }

  @Test
  public void canDetermineSize() {
    $$("#collection li").shouldHave(CollectionCondition.size(50));
  }
}
