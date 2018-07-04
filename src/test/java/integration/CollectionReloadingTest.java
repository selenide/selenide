package integration;

import com.codeborne.selenide.ElementsCollection;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

public class CollectionReloadingTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("collection_with_delays.html");
  }

  @Test
  public void reloadsCollectionOnEveryCall() {
    ElementsCollection collection = $$("#collection li");
    collection.get(0).shouldHave(text("Element #0"));
    collection.get(10).shouldHave(text("Element #10"));
  }

  @Test
  public void canTakeSnapshotOfCollection() {
    ElementsCollection collection = $$("#collection li");
    ElementsCollection snapshot = collection.snapshot();
    int currentSize = snapshot.size();

    collection.shouldHave(sizeGreaterThan(currentSize));
    snapshot.shouldHave(size(currentSize));
  }
}
