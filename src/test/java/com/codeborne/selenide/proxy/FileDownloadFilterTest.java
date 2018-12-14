package com.codeborne.selenide.proxy;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.impl.Downloader;
import com.codeborne.selenide.impl.DummyRandomizer;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class FileDownloadFilterTest implements WithAssertions {
  private FileDownloadFilter filter = new FileDownloadFilter(
    new SelenideConfig().reportsFolder("build/downloads"), new Downloader(new DummyRandomizer("random-text"))
  );
  private HttpResponse response = mock(HttpResponse.class);
  private HttpMessageContents contents = mock(HttpMessageContents.class);
  private HttpMessageInfo messageInfo = mock(HttpMessageInfo.class);

  @BeforeEach
  void setUp() throws IOException {
    DefaultHttpHeaders headers = new DefaultHttpHeaders();
    headers.add("hkey-01", "hvalue-01");
    when(response.headers()).thenReturn(headers);

    when(contents.getContentType()).thenReturn("app/json");
    when(contents.getTextContents()).thenReturn("my-text");
    deleteDirectory(new File("build/downloads/random-text"));
  }

  @Test
  void getsFileNameFromResponseHeader() {
    mockHeaders()
      .add("content-disposition", "attachement; filename=report.pdf")
      .add("referrer", "http://google.kz");

    assertThat(filter.getFileName(response))
      .isEqualTo("report.pdf");
  }

  private HttpHeaders mockHeaders() {
    HttpHeaders headers = new DefaultHttpHeaders();
    when(response.headers()).thenReturn(headers);
    return headers;
  }

  @Test
  void fileNameIsNull_ifResponseDoesNotContainDispositionHeader() {
    mockHeaders()
      .add("location", "/downloads")
      .add("referrer", "http://google.kz");

    assertThat(filter.getFileName(response))
      .isNullOrEmpty();
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

    assertThat(filter.getResponses())
      .isEqualTo("Intercepted 1 responses.\n  null -> 199 \"below 200\" {hkey-01=hvalue-01} app/json  (7 bytes)\n");
  }

  private void mockStatusCode(int code, String reason) {
    when(response.getStatus()).thenReturn(new HttpResponseStatus(code, reason));
  }

  @Test
  void doesNotInterceptResponsesWithCodeAbove300() {
    filter.activate();
    mockStatusCode(300, "300 or above");
    filter.filterResponse(response, contents, messageInfo);

    assertThat(filter.getResponses())
      .isEqualTo("Intercepted 1 responses.\n  null -> 300 \"300 or above\" {hkey-01=hvalue-01} app/json  (7 bytes)\n");
  }

  @Test
  void doesNotInterceptResponsesWithoutDispositionHeader() {
    filter.activate();
    mockStatusCode(200, "200=success");
    mockHeaders();
    filter.filterResponse(response, contents, messageInfo);

    assertThat(filter.getResponses())
      .isEqualTo("Intercepted 1 responses.\n  null -> 200 \"200=success\" {} app/json  (7 bytes)\n");
  }

  @Test
  void interceptsHttpResponse() throws IOException {
    filter.activate();
    mockStatusCode(200, "200=success");
    mockHeaders().add("content-disposition", "attachement; filename=report.pdf");
    when(contents.getBinaryContents()).thenReturn(new byte[]{1, 2, 3, 4, 5});

    filter.filterResponse(response, contents, messageInfo);
    assertThat(filter.getDownloadedFiles().size())
      .isEqualTo(1);

    File file = filter.getDownloadedFiles().get(0);
    assertThat(file.getName()).isEqualTo("report.pdf");
    assertThat(file.getPath()).endsWith("build/downloads/random-text/report.pdf");
    assertThat(readFileToByteArray(file)).isEqualTo(new byte[]{1, 2, 3, 4, 5});
  }
}
