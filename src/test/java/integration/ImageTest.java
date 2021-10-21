package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.image;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ImageTest extends ITest {
  @BeforeEach
  void openTestPageWithImages() {
    openFile("page_with_images.html");
  }

  @Test
  void userCanCheckIfImageIsLoadedCorrectlyUsingCondition() {
    $("#valid-image img").shouldBe(image);
    $("#valid-image").shouldNotBe(image);
    $("h1").shouldNotBe(image);
  }

  @Test
  void isImageConditionFailsForNonImages() {
    assertThatThrownBy(() -> $("h1").shouldBe(image))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be image {h1}")
      .hasMessageContaining("Element: '<h1>Images</h1>'")
      .hasMessageContaining("Actual value: false");
  }

  @Test
  void errorMessage() {
    assertThatThrownBy(() -> $("#invalid-image img").shouldBe(image))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be image {#invalid-image img}")
      .hasMessageContaining("Element: '<img alt=\"missing-logo\" src=\"/files/selenide-unexisting-logo.png\"></img>'")
      .hasMessageContaining("Actual value: false");
  }

  @Test
  void userCanCheckIfImageIsLoadedCorrectly() {
    assertThat($("#valid-image img").isImage())
      .isTrue();
    assertThat($("#invalid-image img").isImage())
      .isFalse();
  }

  @Test
  void isImageIsOnlyApplicableForImages() {
    assertThatThrownBy(() -> $("h1").isImage())
      .isInstanceOf(IllegalArgumentException.class);
  }
}
