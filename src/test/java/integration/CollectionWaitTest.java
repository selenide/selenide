package integration;

import java.util.ArrayList;
import java.util.List;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

class CollectionWaitTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("collection_with_delays.html");
  }

  @Test
  void waitsUntilNthElementAppears() {
    $$("#collection li").get(5).shouldBe(visible);
    $$("#collection li").get(33).shouldBe(visible);
    $$("#collection li").get(49).shouldBe(visible);

    $$("#collection li").first().shouldBe(visible).shouldHave(text("Element #0"));
    $$("#collection li").last().shouldBe(visible).shouldHave(text("Element #49"));
  }

  @Test
  void reproduceStaleElementException_priorToSelenide33() {
    List<SelenideElement> elements = new ArrayList<>($$("h1"));

    executeJavaScript("window.location.reload();");

    elements.get(0).shouldBe(visible).shouldHave(text("Elements will appear soon"));
  }

  @Test
  void failsIfWrongSize() {
    assertThatThrownBy(() -> $$("#collection li").shouldHave(size(-1)))
      .isInstanceOf(AssertionError.class);
  }

  @Test
  void canDetermineSize() {
    $$("#collection li").shouldHave(size(50));
  }
}
