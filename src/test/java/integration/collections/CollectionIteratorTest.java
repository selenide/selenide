package integration.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import integration.ITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("deprecation")
public class CollectionIteratorTest extends ITest {

  private static final List<Integer> reference = asList(0, 1, 2, 3, 4, 5, 6);
  private final ElementsCollection options = $$("#number option");

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects.html");
  }

  @Test
  void canIterateCollection_withIterator() {
    Iterator<SelenideElement> it = options.iterator();
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

  @Test
  void canIterateCollection_withListIterator() {
    ListIterator<SelenideElement> it = options.listIterator(3);
    assertThat(it.hasNext()).isTrue();
    assertThat(it.hasPrevious()).isTrue();
    it.previous().shouldHave(text("Two"));

    assertThat(it.hasPrevious()).isTrue();
    it.previous().shouldHave(text("One"));

    assertThat(it.hasPrevious()).isTrue();
    it.previous().shouldHave(text("Zero"));

    assertThat(it.hasPrevious()).isFalse();

    it.next().shouldHave(text("Zero"));
    assertThat(it.hasPrevious()).isTrue();
  }

  @Test
  void subList() {
    options.shouldHave(size(5)).shouldHave(texts("Zero", "One", "Two", "Three", "Four"));

    List<SelenideElement> subList = options.subList(1, 3);
    assertThat(subList).hasSize(2);
    subList.get(0).shouldHave(text("One"));
    subList.get(1).shouldHave(text("Two"));
  }

  @Test
  void subList_iterator_reference() {
    Iterator<Integer> it = reference.subList(1, 3).iterator();
    assertThat(it.hasNext()).isTrue();
    assertThat(it.next()).isEqualTo(1);
    assertThat(it.hasNext()).isTrue();
    assertThat(it.next()).isEqualTo(2);
    assertThat(it.hasNext()).isFalse();
  }

  @Test
  void subList_iterator() {
    Iterator<SelenideElement> it = options.subList(1, 3).iterator();
    assertThat(it.hasNext()).isTrue();
    assertThat(it.next().getText()).isEqualTo("One");
    assertThat(it.hasNext()).isTrue();
    assertThat(it.next().getText()).isEqualTo("Two");
    assertThat(it.hasNext()).isFalse();
  }

  @Test
  void subList_listIterator_reference() {
    ListIterator<Integer> it = reference.subList(1, 3).listIterator(1);
    assertThat(it.hasNext()).isTrue();
    assertThat(it.hasPrevious()).isTrue();
    assertThat(it.next()).isEqualTo(2);
    assertThat(it.previous()).isEqualTo(2);
    assertThat(it.hasNext()).isTrue();
    assertThat(it.hasPrevious()).isTrue();
  }

  @Test
  void subList_listIterator() {
    ListIterator<SelenideElement> it = options.subList(1, 3).listIterator(1);
    assertThat(it.hasNext()).isTrue();
    assertThat(it.hasPrevious()).isTrue();
    assertThat(it.next().getText()).isEqualTo("Two");
    assertThat(it.previous().getText()).isEqualTo("Two");
    assertThat(it.hasNext()).isTrue();
    assertThat(it.hasPrevious()).isTrue();
  }

  @Test
  void iterator_collectToList() {
    List<String> texts = new ArrayList<>();
    for (SelenideElement e : options.subList(1, 3)) {
      texts.add(e.getText());
    }
    assertThat(texts).containsExactly("One", "Two");
  }
}
