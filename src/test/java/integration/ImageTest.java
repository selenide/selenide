package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImageTest extends IntegrationTest {
  @Before
  public void openTestPageWithImages() {
    openFile("page_with_images.html");
  }

  @Test
  public void userCanCheckIfImageIsLoadedCorrectly() {
    assertTrue($("#valid-image img").isImage());
    assertFalse($("#invalid-image img").isImage());
  }

  @Test(expected = IllegalArgumentException.class)
  public void isImageIsOnlyApplicableForImages() {
    $("h1").isImage();
  }
}
