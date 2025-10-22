package integration.proxy;

import com.codeborne.selenide.impl.FileContent;
import integration.ProxyIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

public class MultipleDownloadsTest extends ProxyIntegrationTest {
  @ParameterizedTest
  @ValueSource(strings = {"empty.html", "hello_world.txt", "download.html"})
  void downloadMultipleFiles(String fileName) {
    timeout = 1;
    openFile("downloadMultipleFiles.html");

    File text = $("#multiple-downloads").download(using(PROXY).withTimeout(4000).withName(fileName));

    assertThat(text.getName()).isEqualTo(fileName);
    assertThat(text.length()).isEqualTo(new FileContent(fileName).content().length());
  }
}
