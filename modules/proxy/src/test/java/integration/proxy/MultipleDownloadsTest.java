package integration.proxy;

import com.codeborne.selenide.impl.FileContent;
import integration.ProxyIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.files.FileFilters.withName;
import static org.assertj.core.api.Assertions.assertThat;

public class MultipleDownloadsTest extends ProxyIntegrationTest {
  @Test
  void downloadMultipleFiles() throws FileNotFoundException {
    open("/downloadMultipleFiles.html");

    File text = $("#multiple-downloads").download(
      using(PROXY).withTimeout(4000).withFilter(withName("empty.html"))
    );

    assertThat(text.getName()).isEqualTo("empty.html");
    assertThat(text.length()).isEqualTo(new FileContent("empty.html").content().length());
  }
}
