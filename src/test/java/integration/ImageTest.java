package integration;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageTest extends ITest {
  @BeforeEach
  void openTestPageWithImages() {
    openFile("page_with_images.html");
  }

  @Test
  void userCanCheckIfImageIsLoadedCorrectly() {
    Assumptions.assumeFalse(browser().isHtmlUnit());

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
