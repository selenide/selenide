package integration.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThat;

public class CollectionIteratorTest extends ITest {

  private final ElementsCollection options = $$("#number option");

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects.html");
  }

  @Test
  void canIterateCollection_withIterator() {
    Iterator<SelenideElement> it = options.asFixedIterable().iterator();
    assertThat(it.hasNext()).isTrue();
    it.next().shouldHave(text("Zero"));

    assertThat(it.hasNext()).isTrue();
    it.next().shouldHave(text("One"));

    assertThat(it.hasNext()).isTrue();
    it.next().shouldHave(text("Two"));

    assertThat(it.hasNext()).isTrue();
    it.next().shouldHave(text("Three"));

    assertThat(it.hasNext()).isTrue();
    it.next().shouldHave(text("Four"));

    assertThat(it.hasNext()).isFalse();
  }
}
