package integration;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.print.PrintOptions;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static com.codeborne.selenide.Selenide.open;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.assertj.core.api.Assertions.assertThat;

public class PrintDocumentAsPdfTest extends IntegrationTest {
  @Test
  void printPdf() throws IOException {
    open("https://ru.selenide.org");
    PrintsPage driver = (PrintsPage) WebDriverRunner.getWebDriver();
    Pdf pdf = driver.print(new PrintOptions());
    byte[] bytes = Base64.getDecoder().decode(pdf.getContent());
    File out = new File("/Users/andrei/projects/selenide/some.pdf");
    writeByteArrayToFile(out, bytes);
    assertThat(out).exists();
  }
}
