package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static com.codeborne.selenide.CollectionCondition.size;
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
    it.next().shouldHave(text("Item #1"));
    it.next().shouldHave(text("Item #2"));
    it.next().shouldHave(text("Item #3"));
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
