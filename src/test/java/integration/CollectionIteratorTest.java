package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.ListIterator;

import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThat;

public class CollectionIteratorTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects.html");
  }

  @Test
  void canIterateCollection_withIterator() {
    Iterator<SelenideElement> it = $$("select#number option").iterator();
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
    ListIterator<SelenideElement> it = $$("select#number option").listIterator(3);
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
}
