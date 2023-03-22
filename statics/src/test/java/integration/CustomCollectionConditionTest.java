package integration;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.Selenide.$$;
import static java.lang.System.lineSeparator;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ParametersAreNonnullByDefault
final class CustomCollectionConditionTest extends IntegrationTest {

  @BeforeEach
  void openTestPage() {
    openFile("page_with_list_of_elements.html");
    Configuration.timeout = 1;
  }

  @Test
  void canUseCustomCollectionCondition() {
    $$(".element").last(2).shouldHave(allTextsStartingWith("T"));
  }

  @Test
  void errorMessageOfCustomCondition() {
    assertThatThrownBy(() ->
      $$(".element").shouldHave(allTextsStartingWith("T"))
    )
      .isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("Texts not starting with prefix")
      .hasMessageContaining("Actual: [One, Two, Three]")
      .hasMessageContaining("Expected: prefix \"T\"")
      .hasMessageContaining("Collection: .element");
  }

  @Nonnull
  private CollectionCondition allTextsStartingWith(String prefix) {
    return new CollectionCondition() {
      @Override
      public boolean test(List<WebElement> webElements) {
        return webElements.stream()
          .map(webElement -> webElement.getText().startsWith(prefix))
          .reduce(true, (x, y) -> x && y);
      }

      @Override
      public void fail(CollectionSource collection,
                       @Nullable List<WebElement> elements,
                       @Nullable Exception lastError,
                       long timeoutMs) {
        List<String> actualTexts = ElementsCollection.texts(elements);
        String expected = "prefix \"" + prefix + '"';

        throw new MismatchingPrefixesError(expected, actualTexts, collection);
      }

      @Override
      public boolean missingElementSatisfiesCondition() {
        return false;
      }

      @Override
      public String toString() {
        return "All texts starting with " + prefix;
      }

      class MismatchingPrefixesError extends UIAssertionError {
        MismatchingPrefixesError(String expected, List<String> actualTexts, CollectionSource collection) {
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
    };
  }
}
