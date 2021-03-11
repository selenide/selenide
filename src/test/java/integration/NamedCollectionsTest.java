package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ListSizeMismatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.enabled;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NamedCollectionsTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canGiveCollectionHumanReadableName() {
    assertThatThrownBy(() -> {
      $$x("/long/ugly/xpath").as("Login buttons").shouldHave(size(666));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons");
  }

  @Test
  void canGiveCollectionHumanReadableName_filtered_and_named() {
    assertThatThrownBy(() -> {
      $$x("/long/ugly/xpath").as("Login buttons").filter(enabled).as("enabled buttons").shouldHave(size(666));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 666, actual: 0, collection: enabled buttons");
  }

  @Test
  void canGiveCollectionHumanReadableName_named_and_filtered() {
    assertThatThrownBy(() -> {
      $$x("/long/ugly/xpath").as("Login buttons").filter(enabled).shouldHave(size(666));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons.filter(enabled)");
  }

  @Test
  void canGiveCollectionHumanReadableName_wrapped() {
    List<WebElement> unfiltered = driver().getWebDriver().findElements(By.xpath("/long/ugly/xpath"));
    assertThatThrownBy(() -> {
      ElementsCollection unnamed = driver().$$(unfiltered);
      unnamed.as("Login buttons").filter(enabled).shouldHave(size(666));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons.filter(enabled)");
  }

  @Test
  void canGiveCollectionHumanReadableName_snapshot() {
    assertThatThrownBy(() -> {
      $$x("/long/ugly/xpath").snapshot().as("Login buttons").shouldHave(size(666));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons");
  }

  @Test
  void collectionDescription_head() {
    assertThatThrownBy(() -> {
      $$x("/long/ugly/xpath").first(42).shouldHave(size(666));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 666, actual: 0, collection: By.xpath: /long/ugly/xpath:first(42)");
  }

  @Test
  void canGiveCollectionHumanReadableName_head() {
    assertThatThrownBy(() -> {
      $$x("/long/ugly/xpath").first(42).as("Login buttons").shouldHave(size(666));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons");
  }

  @Test
  void canGiveCollectionHumanReadableName_tail() {
    assertThatThrownBy(() -> {
      $$x("/long/ugly/xpath").last(42).as("Login buttons").shouldHave(size(666));
    })
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons");
  }
}
