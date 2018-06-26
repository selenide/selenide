package integration;

import org.junit.jupiter.api.Assertions;
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

    Assertions.assertTrue($("#valid-image img").isImage());
    Assertions.assertFalse($("#invalid-image img").isImage());
  }

  @Test
  void isImageIsOnlyApplicableForImages() {
    Assertions.assertThrows(IllegalArgumentException.class,
      () -> $("h1").isImage());
  }
}
