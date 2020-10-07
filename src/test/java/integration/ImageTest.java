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
      .isInstanceOf(ElementShould.class);
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
