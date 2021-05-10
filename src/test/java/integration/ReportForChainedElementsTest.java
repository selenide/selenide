package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static java.time.Duration.ofMillis;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ReportForChainedElementsTest extends ITest {
  private List<File> previousScreenshots;

  @BeforeEach
  void openTestPage() {
    setTimeout(1);
    openFile("page_with_absence.html");
    previousScreenshots = screenshots();
  }

  @Test
  void shouldShowWhichElementInChainWasNotFound() {
    assertThatThrownBy(() -> {
      $("body")
        .$(".h1")
        .$(".h2")
        .$(".h3")
        .$(".h4")
        .$(".h5")
        .$(".h6")
        .shouldBe(visible, ofMillis(400));
    })
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {body/.h1/.h2/.h3/.h4}");

    assertTookScreenshots();
  }

  @Test
  void shouldShowAliasInElementNotFoundError() {
    assertThatThrownBy(() -> {
      $("body").as("-the-body-")
        .$(".h1").as("-the-h1-")
        .$(".h2").as("-the-h2-")
        .$(".h3").as("-the-h3-")
        .$(".h4").as("-the-h4-")
        .$(".h5").as("-the-h5-")
        .$(".h6").as("-the-h6-")
        .shouldBe(visible, ofMillis(400));
    })
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {-the-h4-}");

    assertTookScreenshots();
  }

  private void assertTookScreenshots() {
    List<File> screenshots = new ArrayList<>(screenshots());
    screenshots.removeAll(previousScreenshots);
    assertThat(screenshots).as("*.png + *.html").hasSize(2);
  }

  private List<File> screenshots() {
    File dir = screenshotsFolder();
    File[] files = dir.listFiles();
    return files == null ? emptyList() : asList(files);
  }

  @Nonnull
  private File screenshotsFolder() {
    return new File(driver().config().reportsFolder());
  }
}
