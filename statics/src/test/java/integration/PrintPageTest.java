package integration;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.print.PrintOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static com.codeborne.pdftest.assertj.Assertions.assertThat;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.WebDriverRunner.isSafari;
import static org.assertj.core.api.Assumptions.assumeThat;

final class PrintPageTest extends ITest {
  private static final Logger log = LoggerFactory.getLogger(PrintPageTest.class);

  @BeforeEach
  void openTestPage() {
    openFile("page_for_printing.html");
  }

  @Test
  void onScreen() {
    $("h1").shouldBe(hidden);
    $("h2").shouldBe(visible).shouldHave(text("Colourful text"));
    $("h3").shouldBe(visible).shouldHave(text("For screens only"));
  }

  @Test
  void onPrinter() throws IOException {
    assumeThat(isSafari()).isFalse();

    PrintsPage driver = (PrintsPage) driver().getWebDriver();
    Pdf pdf = driver.print(new PrintOptions());

    saveToFile(pdf);

    PDF content = new PDF(Base64.getDecoder().decode(pdf.getContent()));
    assertThat(content).containsExactText("Hello, printer!");
  }

  private void saveToFile(Pdf pdf) throws IOException {
    byte[] bytes = Base64.getDecoder().decode(pdf.getContent());
    File out = new File(Configuration.reportsFolder + "/printed-page.pdf");
    FileUtils.writeByteArrayToFile(out, bytes);
    log.info("[[ATTACHMENT|{}]]", out.getAbsolutePath());
  }
}
