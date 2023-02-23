package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static com.codeborne.selenide.WebDriverRunner.isHeadless;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

public class PrintAsPdfTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    assumeThat(isHeadless()).isFalse();
    assumeThat(isChrome() || isEdge());
  }

  @Test
  void printDocumentAsPdf() throws FileNotFoundException {
    openFile("page_with_print.html");
    $("#button-print-cv").shouldHave(text("Print CV"));

    File cv = $("#button-print-cv").download(
      using(FOLDER).withFilter(withExtension("pdf")).withTimeout(6_000)
    );
    assertThat(cv).isFile();
    assertThat(cv).hasName("page with print.pdf");
  }
}
