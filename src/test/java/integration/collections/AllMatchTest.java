package integration.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.MatcherError;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.function.Predicate;

import static com.codeborne.selenide.CollectionCondition.allMatch;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AllMatchTest extends ITest {

  @BeforeEach
  void openPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void checks_that_all_elements_match_given_predicate() {
    $$("#radioButtons input").should(allMatch("name==me",
      el -> "me".equals(el.getAttribute("name")))
    );
  }

  @Test
  void doesNotAcceptEmptyList() {
    assertThatThrownBy(() -> $$(".missing").should(allMatch("all fine", el -> true)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.missing}")
      .hasMessageContaining("Expected: all elements to match [all fine] predicate");
  }

  @Test
  void errorMessage() {
    ElementsCollection elements = $$("#radioButtons input");
    Predicate<WebElement> isCat = el -> "cat".equals(el.getAttribute("value"));

    assertThatThrownBy(() -> elements.should(allMatch("value==cat", isCat)))
      .isInstanceOf(MatcherError.class)
      .hasMessageStartingWith("Collection matcher error")
      .hasMessageContaining("Expected: all elements to match [value==cat] predicate")
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
