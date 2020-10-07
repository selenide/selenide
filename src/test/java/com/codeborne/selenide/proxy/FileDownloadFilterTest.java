package com.codeborne.selenide.proxy;

import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.impl.Downloader;
import com.codeborne.selenide.impl.DummyRandomizer;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class FileDownloadFilterTest implements WithAssertions {
  private final FileDownloadFilter filter = new FileDownloadFilter(
    new SelenideConfig().downloadsFolder("build/downloads"), new Downloader(new DummyRandomizer("random-text"))
  );
  private final HttpResponse response = mock(HttpResponse.class);
  private final HttpMessageContents contents = mock(HttpMessageContents.class);
  private final HttpMessageInfo messageInfo = mock(HttpMessageInfo.class);

  @BeforeEach
  void setUp() throws IOException {
    DefaultHttpHeaders headers = new DefaultHttpHeaders();
    headers.add("hkey-01", "hvalue-01");
    when(response.headers()).thenReturn(headers);

    when(contents.getContentType()).thenReturn("app/json");
    when(contents.getTextContents()).thenReturn("my-text");
    deleteDirectory(new File("build/downloads/random-text"));
  }

  private HttpHeaders mockHeaders() {
    HttpHeaders headers = new DefaultHttpHeaders();
    when(response.headers()).thenReturn(headers);
    return headers;
  }

  @Test
  void doesNothingIfNotActivated() {
    filter.deactivate();
    filter.filterResponse(response, contents, messageInfo);

    verifyNoMoreInteractions(response);
    verifyNoMoreInteractions(contents);
    verifyNoMoreInteractions(messageInfo);
  }

  @Test
  void doesNotInterceptResponsesWithCodeBelow200() {
    filter.activate();
    mockStatusCode(199, "below 200");
    filter.filterResponse(response, contents, messageInfo);

    assertThat(filter.responsesAsString())
      .isEqualTo("Intercepted 1 responses:\n  #1  null -> 199 \"below 200\" {hkey-01=hvalue-01} app/json  (7 bytes)\n");
  }

  private void mockStatusCode(int code, String reason) {
    when(response.status()).thenReturn(new HttpResponseStatus(code, reason));
  }

  private void mockUrl(String url) {
    when(messageInfo.getUrl()).thenReturn(url);
  }

  @Test
  void doesNotInterceptResponsesWithCodeAbove300() {
    filter.activate();
    mockStatusCode(300, "300 or above");
    filter.filterResponse(response, contents, messageInfo);

    assertThat(filter.responsesAsString())
      .isEqualTo("Intercepted 1 responses:\n  #1  null -> 300 \"300 or above\" {hkey-01=hvalue-01} app/json  (7 bytes)\n");
  }

  @Test
  void interceptsHttpResponse() throws IOException {
    filter.activate();
    mockStatusCode(200, "200=success");
    mockHeaders().add("content-disposition", "attachement; filename=report.pdf");
    when(contents.getBinaryContents()).thenReturn(new byte[]{1, 2, 3, 4, 5});

    filter.filterResponse(response, contents, messageInfo);
    assertThat(filter.downloads().size()).isEqualTo(1);

    File file = filter.downloads().files().get(0).getFile();
    File expectedFile = new File("build/downloads/random-text/report.pdf");
    assertThat(file.getName()).isEqualTo("report.pdf");
    assertThat(file.getPath()).endsWith(expectedFile.getPath());
    assertThat(readFileToByteArray(file)).isEqualTo(new byte[]{1, 2, 3, 4, 5});
  }

  @Test
  void usesNameFromURL_ifResponseHasNoContentDispositionHeader() throws IOException {
    filter.activate();
    mockStatusCode(200, "200=success");
    mockHeaders();
    mockUrl("/foo/bar/cv.pdf?42");
    when(contents.getBinaryContents()).thenReturn("HELLO".getBytes(UTF_8));

    filter.filterResponse(response, contents, messageInfo);

    assertThat(filter.responsesAsString())
      .isEqualTo("Intercepted 1 responses:\n  #1  /foo/bar/cv.pdf?42 -> 200 \"200=success\" {} app/json  (7 bytes)\n");
    File file = filter.downloads().files().get(0).getFile();
    File expectedFile = new File("build/downloads/random-text/cv.pdf");
    assertThat(file.getName()).isEqualTo("cv.pdf");
    assertThat(file.getPath()).endsWith(expectedFile.getPath());
    assertThat(readFileToString(file, UTF_8)).isEqualTo("HELLO");
  }
}
