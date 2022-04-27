package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.text;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

final class CollectionWithChangingTextsTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("collection_with_changing_texts.html");
    setTimeout(4000);
  }

  @Test
  void snapshotDoesNotReloadElements() {
    Iterator<SelenideElement> it = $$("#collection li").snapshot()
      .shouldHave(size(3))
      .iterator();
    it.next().shouldHave(partialText("Item #1"));
    it.next().shouldHave(partialText("Item #2"));
    it.next().shouldHave(partialText("Item #3"));
    assertThat(it.hasNext()).isFalse();
  }

  @Test
  void iteratorReloadsCollectionOnEveryCall() {
    Iterator<SelenideElement> it = $$("#collection li").iterator();
    it.next().shouldHave(text("Item #1"));
    it.next().shouldHave(text("Item #2"));
    it.next().shouldHave(text("Item #3"));
    assertThat(it.hasNext()).isFalse();
  }

  @Test
  void reloadingIterable_reloadsCollectionOnEveryCall() throws InterruptedException {
    Iterator<SelenideElement> it = $$("#collection li").asDynamicIterable().iterator();
    it.next().shouldHave(text("Item #1"));
    it.next().shouldHave(text("Item #2"));
    it.next().shouldHave(text("Item #3"));
    waitForNewElementsRendered();
    it.next().shouldHave(text("Additional text #4"));
    it.next().shouldHave(text("Additional text #5"));
    assertThat(it.hasNext()).isFalse();
  }

  @Test
  void stream() {
    List<String> texts = $$("#collection li").asDynamicIterable()
      .stream()
      .map(se -> se.getText()).collect(Collectors.toList());

    assertThat(texts).containsExactly("Item #1", "Item #2", "Item #3");
  }

  @Test
  void stream_afterNewElementsAppeared() throws InterruptedException {
    waitForNewElementsRendered();

    List<String> texts = $$("#collection li").asDynamicIterable()
      .stream().map(se -> se.getText()).collect(Collectors.toList());

    assertThat(texts).containsExactly(
      "Updated Item #1", "Updated Item #2", "Updated Item #3",
      "Additional text #4", "Additional text #5"
    );
  }

  @Test
  void fixedIterable_doesNotReloadCollectionOnEveryCall() throws InterruptedException {
    Iterator<SelenideElement> it = $$("#collection li").asFixedIterable().iterator();

    assertThat(it.hasNext()).isTrue();
    it.next().shouldHave(text("Item #1"));
    it.next().shouldHave(text("Item #2"));
    it.next().shouldHave(text("Item #3"));
    waitForNewElementsRendered();
    assertThat(it.hasNext()).isFalse();
  }

  // File "collection_with_changing_texts.html" contains JavaScript which adds new elements after 340 ms.
  private void waitForNewElementsRendered() throws InterruptedException {
    sleep(350);
  }
}
