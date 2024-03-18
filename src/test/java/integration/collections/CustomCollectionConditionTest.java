package integration.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.ElementCommunicator;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.impl.Plugins.inject;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ParametersAreNonnullByDefault
final class CustomCollectionConditionTest extends ITest {

  @BeforeEach
  void openTestPage() {
    openFile("page_with_list_of_elements.html");
  }

  @Test
  void canUseCustomCollectionCondition() {
    $$(".element").shouldHave(texts("One", "Two", "Three"));
    $$(".element").last(2).shouldHave(allTextsStartingWith("T"));
  }

  @Test
  void customConditionExample_or() {
    $$(".element").shouldHave(texts("One", "Two", "Three"));
    $$(".element").first(2).shouldHave(or(texts("One", "None"), texts("One", "Two")));
  }

  @Test
  void errorMessageOfCustomCondition() {
    assertThatThrownBy(() ->
      $$(".element").shouldHave(allTextsStartingWith("T"))
    )
      .isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("Texts not starting with prefix")
      .hasMessageContaining("Actual: [One, Two, Three]")
      .hasMessageContaining("Expected: All texts starting with T")
      .hasMessageContaining("Collection: .element");
  }

  private WebElementsCondition allTextsStartingWith(String prefix) {
    return new AllTextsStartingWith(prefix);
  }

  private Or or(WebElementsCondition c1, WebElementsCondition c2) {
    return new Or(c1, c2);
  }

  private static class AllTextsStartingWith extends WebElementsCondition {
    private static final ElementCommunicator communicator = inject(ElementCommunicator.class);

    private final String prefix;

    private AllTextsStartingWith(String prefix) {
      this.prefix = prefix;
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CheckResult check(Driver driver, List<WebElement> elements) {
      List<String> actualTexts = communicator.texts(driver, elements);
      Boolean passed = elements.stream()
        .map(webElement -> webElement.getText().startsWith(prefix))
        .reduce(true, (x, y) -> x && y);

      return new CheckResult(passed, actualTexts);
    }

    @Override
    public String errorMessage() {
      return "Texts not starting with prefix " + prefix;
    }

    @Override
    public String toString() {
      return "All texts starting with " + prefix;
    }
  }

  private static class Or extends WebElementsCondition {
    private final WebElementsCondition c1;
    private final WebElementsCondition c2;

    private Or(WebElementsCondition c1, WebElementsCondition c2) {
      this.c1 = c1;
      this.c2 = c2;
    }

    @Nonnull
    @Override
    public CheckResult check(CollectionSource collection) {
      CheckResult r1 = c1.check(collection);
      return r1.verdict() == ACCEPT ? r1 : c2.check(collection);
    }

    @Override
    public String toString() {
      return "%s OR %s".formatted(c1, c2);
    }
  }
}
