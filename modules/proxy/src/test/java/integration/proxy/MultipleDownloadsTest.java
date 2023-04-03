package integration.proxy;

import com.codeborne.selenide.impl.FileContent;
import integration.ProxyIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.files.FileFilters.withName;
import static org.assertj.core.api.Assertions.assertThat;

public class MultipleDownloadsTest extends ProxyIntegrationTest {
  @ParameterizedTest
  @ValueSource(strings = {"empty.html", "hello_world.txt", "download.html"})
  void downloadMultipleFiles(String fileName) throws FileNotFoundException {
    openFile("downloadMultipleFiles.html");

    File text = $("#multiple-downloads").download(
      using(PROXY).withTimeout(4000).withFilter(withName(fileName))
    );

    assertThat(text.getName()).isEqualTo(fileName);
    assertThat(text.length()).isEqualTo(new FileContent(fileName).content().length());
  }
}
