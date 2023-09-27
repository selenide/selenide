package integration.collections;

import com.codeborne.selenide.SelenideElement;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.innerText;
import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThat;

final class CollectionElementTextTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_invisible_spaces.html");
  }

  @Test
  void singleElementText_stripsInvisibleSpaces() {
    SelenideElement lastName = $("#hero #last-name");
    assertThat(lastName.getText()).isEqualTo("Mc Clane");
    assertThat(lastName.toWebElement().getText()).isEqualTo("Mc Clane");

    lastName.shouldHave(text("Mc Clane"));
    lastName.shouldHave(exactText("Mc Clane"));
    $$("#hero span").get(1).shouldHave(text("Mc Clane"));
  }

  @Test
  void stripsInvisibleSpacesFromText() {
    $$("#hero span").shouldHave(texts("John", "Mc Clane"));
    $$("#hero").shouldHave(texts("John Mc Clane"));
  }

  @Test
  void innerText_doesNotStripInvisibleSpaces() {
    $$("#hero span").get(1).shouldHave(innerText("Mc Cl​ane"));
  }

  @Test
  void ownText_doesNotStripInvisibleSpaces() {
    $$("#hero span").get(1).shouldHave(ownText("Mc Cl\u200Bane"));
  }
}
