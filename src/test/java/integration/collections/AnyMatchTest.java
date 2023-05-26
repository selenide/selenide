package integration.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.MatcherError;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AnyMatchTest extends ITest {

  private final CollectionCondition containCat = anyMatch("value==cat", el -> "cat".equals(el.getAttribute("value")));
  private final CollectionCondition containDog = anyMatch("value==dog", el -> "dog".equals(el.getAttribute("value")));

  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void checks_that_some_of_elements_matches_given_predicate() {
    $$("#radioButtons input").should(containCat);
  }

  @Test
  void doesNotAcceptEmptyList() {
    assertThatThrownBy(() -> $$(".missing").should(anyMatch("good enough", el -> true)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.missing}")
      .hasMessageContaining("Expected: any of elements to match [good enough] predicate");
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $$("#radioButtons input").should(containDog))
      .isInstanceOf(MatcherError.class)
      .hasMessageStartingWith("Collection matcher error")
      .hasMessageContaining("Expected: any of elements to match [value==dog] predicate")
      .hasMessageContaining("Collection: #radioButtons input")
      .hasMessageContaining("""
        Elements: [
        \t<input name="me" type="radio" value="master"></input>,
        \t<input name="me" type="radio" value="margarita"></input>,
        \t<input name="me" type="radio" value="cat"></input>,
        \t<input name="me" type="radio" value="woland"></input>
        ]""");
  }
}
