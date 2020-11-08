package integration.proxy;

import com.codeborne.selenide.files.FileFilters;
import integration.IntegrationTest;
import org.junit.jupiter.api.*;

import java.io.*;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selenide.*;

public class MultipleDownloadsTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    closeWebDriver();
    useProxy(true);
  }
  @Test
  void downloadMultipleFiles() throws FileNotFoundException {
    open("/downloadMultipleFiles.html");
    File text = $("#two-downloads").download(using(PROXY)
      .withTimeout(6000).withFilter(FileFilters.withName("empty.html")));
    Assertions.assertEquals("empty.html", text.getName());
    Assertions.assertEquals(224, text.length());
  }
}
