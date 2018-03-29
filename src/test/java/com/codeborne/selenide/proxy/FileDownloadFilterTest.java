package com.codeborne.selenide.proxy;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FileDownloadFilterTest {
  FileDownloadFilter filter = new FileDownloadFilter();
  HttpResponse response = mock(HttpResponse.class);
  HttpMessageContents contents = mock(HttpMessageContents.class);
  HttpMessageInfo messageInfo = mock(HttpMessageInfo.class);

  @Before
  public void setUp() {
    DefaultHttpHeaders headers = new DefaultHttpHeaders();
    headers.add("hkey-01", "hvalue-01");
    when(response.headers()).thenReturn(headers);

    when(contents.getContentType()).thenReturn("app/json");
    when(contents.getTextContents()).thenReturn("my-text");
  }

  @Test
  public void getsFileNameFromResponseHeader() {
    mockHeaders()
        .add("content-disposition", "attachement; filename=report.pdf")
        .add("referrer", "http://google.kz");

    assertThat(filter.getFileName(response), is("report.pdf"));
  }

  @Test
  public void fileNameIsNull_ifResponseDoesNotContainDispositionHeader() {
    mockHeaders()
        .add("location", "/downloads")
        .add("referrer", "http://google.kz");

    assertNull(filter.getFileName(response));
  }

  @Test
  public void doesNothingIfNotActivated() {
    filter.deactivate();
    filter.filterResponse(response, contents, messageInfo);

    verifyNoMoreInteractions(response);
    verifyNoMoreInteractions(contents);
    verifyNoMoreInteractions(messageInfo);
  }

  @Test
  public void doesNotInterceptResponsesWithCodeBelow200() {
    filter.activate();
    mockStatusCode(199, "below 200");
    filter.filterResponse(response, contents, messageInfo);

    assertThat(filter.getResponses(), is("Intercepted 1 responses." +
        "\n  null -> 199 \"below 200\" {hkey-01=hvalue-01} app/json  (7 bytes)\n"));
  }

  private void mockStatusCode(int code, String reason) {
    when(response.getStatus()).thenReturn(new HttpResponseStatus(code, reason));
  }

  @Test
  public void doesNotInterceptResponsesWithCodeAbove300() {
    filter.activate();
    mockStatusCode(300, "300 or above");
    filter.filterResponse(response, contents, messageInfo);

    assertThat(filter.getResponses(), is("Intercepted 1 responses." +
        "\n  null -> 300 \"300 or above\" {hkey-01=hvalue-01} app/json  (7 bytes)\n"));
  }

  @Test
  public void doesNotInterceptResponsesWithoutDispositionHeader() {
    filter.activate();
    mockStatusCode(200, "200=success");
    mockHeaders();
    filter.filterResponse(response, contents, messageInfo);

    assertThat(filter.getResponses(), is("Intercepted 1 responses.\n  null -> 200 \"200=success\" {} app/json  (7 bytes)\n"));
  }

  @Test
  public void interceptsHttpResponse() throws IOException {
    filter.activate();
    mockStatusCode(200, "200=success");
    mockHeaders().add("content-disposition", "attachement; filename=report.pdf");
    when(contents.getBinaryContents()).thenReturn(new byte[]{1, 2, 3, 4, 5});

    filter.filterResponse(response, contents, messageInfo);
    assertThat(filter.getDownloadedFiles().size(), is(1));

    File file = filter.getDownloadedFiles().get(0);
    assertThat(file.getName(), is("report.pdf"));
    assertThat(readFileToByteArray(file), is(new byte[]{1, 2, 3, 4, 5}));
  }

  private HttpHeaders mockHeaders() {
    HttpHeaders headers = new DefaultHttpHeaders();
    when(response.headers()).thenReturn(headers);
    return headers;
  }
}
