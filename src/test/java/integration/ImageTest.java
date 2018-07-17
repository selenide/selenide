package integration;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;

class ImageTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithImages() {
    openFile("page_with_images.html");
  }

  @Test
  void userCanCheckIfImageIsLoadedCorrectly() {
    Assumptions.assumeFalse(isHtmlUnit());

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
