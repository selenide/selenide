package i2;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import integration.SelenoidSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SelenoidSetup.class)
public class FileDownloadTest {
  @BeforeEach
  void setUp() {
    Configuration.fileDownload = FileDownloadMode.FOLDER;
  }

  @Test
  void slowDownload() throws IOException {
    String fileContent = rightPad("Lorem ipsum dolor sit amet", 4096, "\nlaborum");
    open("https://selenide.org/test-page/download.html");
    $("[name=delay]").setValue("3000");
    $("#lore-ipsum").setValue(fileContent);

    File file = $("#slow-download").download(withExtension("txt"));

    assertThat(file).hasName("hello.txt");
    assertThat(file).content().isEqualToIgnoringWhitespace(fileContent);
  }
}