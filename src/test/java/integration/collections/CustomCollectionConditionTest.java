package integration.collections;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.texts;
import static java.lang.System.lineSeparator;
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
  void errorMessageOfCustomCondition() {
    assertThatThrownBy(() ->
      $$(".element").shouldHave(allTextsStartingWith("T"))
    )
      .isInstanceOf(MismatchingPrefixesError.class)
      .hasMessageStartingWith("Texts not starting with prefix")
      .hasMessageContaining("Actual: [One, Two, Three]")
      .hasMessageContaining("Expected: All texts starting with T")
      .hasMessageContaining("Collection: .element");
  }

  private CollectionCondition allTextsStartingWith(String prefix) {
    return new AllTextsStartingWith(prefix);
  }

  private static class AllTextsStartingWith extends CollectionCondition {
    private final String prefix;

    private AllTextsStartingWith(String prefix) {
      this.prefix = prefix;
    }

    @Nonnull
    @CheckReturnValue
    @Override
    public CheckResult check(CollectionSource collection) {
      List<WebElement> elements = collection.getElements();
      List<String> actualTexts = ElementsCollection.texts(elements);
      return new CheckResult(elements.stream()
        .map(webElement -> webElement.getText().startsWith(prefix))
        .reduce(true, (x, y) -> x && y), actualTexts);
    }

    @Override
    public void fail(CollectionSource collection,
                     CheckResult lastCheckResult,
                     @Nullable Exception cause,
                     long timeoutMs) {
      throw new MismatchingPrefixesError(toString(), lastCheckResult.getActualValue(), collection, explanation);
    }

    @Override
    public boolean missingElementSatisfiesCondition() {
      return false;
    }

    @Override
    public String toString() {
      return "All texts starting with " + prefix;
    }
  }

  private static class MismatchingPrefixesError extends UIAssertionError {
    MismatchingPrefixesError(String expected, @Nullable List<String> actualTexts, CollectionSource collection,
                             @Nullable String explanation) {
      super(
        collection.driver(),
        "Texts not starting with prefix " +
        lineSeparator() + "Actual: " + actualTexts +
        lineSeparator() + "Expected: " + expected +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description(),
        expected, actualTexts);
    }
  }
}
