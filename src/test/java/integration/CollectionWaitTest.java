package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

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

  @Test(expected = AssertionError.class)
  public void failsIfWrongSize() {
    $$("#collection li").shouldHave(size(-1));
  }

  @Test
  public void canDetermineSize() {
    $$("#collection li").shouldHave(size(50));
  }
}
