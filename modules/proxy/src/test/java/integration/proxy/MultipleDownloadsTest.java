package integration.proxy;

import com.codeborne.selenide.impl.FileContent;
import integration.ProxyIntegrationTest;
import org.junit.jupiter.api.RepeatedTest;

import java.io.File;
import java.io.FileNotFoundException;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.files.FileFilters.withName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleDownloadsTest extends ProxyIntegrationTest {
  @RepeatedTest(10)
  void downloadMultipleFiles() throws FileNotFoundException {
    open("/downloadMultipleFiles.html");

    File text = $("#multiple-downloads").download(
      using(PROXY).withTimeout(4000).withFilter(withName("empty.html"))
    );

    assertEquals("empty.html", text.getName());
    assertEquals(new FileContent("empty.html").content().length(), text.length());
  }
}
