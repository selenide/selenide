package integration;

import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.isImage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageTest extends ITest {
  @BeforeEach
  void openTestPageWithImages() {
    openFile("page_with_images.html");
  }

  @Test
  void userCanCheckIfImageIsLoadedCorrectlyUsingCondition() {
    $("#valid-image img").should(isImage);
    $("#valid-image").shouldNot(isImage);
    $("h1").shouldNotBe(isImage);
  }

  @Test
  void isImageConditionFailsForNonImages() {
    assertThatThrownBy(() -> $("h1").shouldBe(isImage))
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
