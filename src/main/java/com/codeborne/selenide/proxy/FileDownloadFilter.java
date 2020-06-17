package com.codeborne.selenide.proxy;

import com.browserup.bup.filters.ResponseFilter;
import com.browserup.bup.util.HttpMessageContents;
import com.browserup.bup.util.HttpMessageInfo;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.Downloader;
import com.codeborne.selenide.impl.Downloads;
import com.codeborne.selenide.impl.HttpHelper;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@ParametersAreNonnullByDefault
public class FileDownloadFilter implements ResponseFilter {
  private static final Logger log = LoggerFactory.getLogger(FileDownloadFilter.class);

  private final Config config;
  private final Downloader downloader;

  private final HttpHelper httpHelper = new HttpHelper();
  private boolean active;
  private final Downloads downloads = new Downloads();
  private final List<Response> responses = new CopyOnWriteArrayList<>();

  public FileDownloadFilter(Config config) {
    this(config, new Downloader());
  }

  FileDownloadFilter(Config config, Downloader downloader) {
    this.config = config;
    this.downloader = downloader;
  }

  /**
   * Activate this filter.
   * Starting from this moment, it will record all responses that seem to be a "file download".
   */
  public void activate() {
    reset();
    active = true;
  }

  public void reset() {
    downloads.clear();
    responses.clear();
  }

  /**
   * Deactivate this filter.
   * Starting from this moment, it will not record any responses.
   */
  public void deactivate() {
    active = false;
  }

  @Override
  public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
    if (!active) return;

    Response r = new Response(messageInfo.getUrl(),
      response.status().code(),
      response.status().reasonPhrase(),
      toMap(response.headers()),
      contents.getContentType(),
      contents.getTextContents()
    );
    responses.add(r);

    if (response.status().code() < 200 || response.status().code() >= 300) return;

    String fileName = getFileName(r);

    File file = downloader.prepareTargetFile(config, fileName);
    try {
      FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
      downloads.add(new DownloadedFile(file, r.headers));
    }
    catch (IOException e) {
      log.error("Failed to save downloaded file to {} for url {}", file.getAbsolutePath(), messageInfo.getUrl(), e);
    }
  }

  private Map<String, String> toMap(HttpHeaders headers) {
    Map<String, String> map = new HashMap<>();
    for (Map.Entry<String, String> header : headers) {
      map.put(header.getKey().toLowerCase(), header.getValue());
    }
    return map;
  }

  /**
   * @return list of all downloaded files since activation.
   */
  @CheckReturnValue
  @Nonnull
  public Downloads downloads() {
    return downloads;
  }

  @CheckReturnValue
  @Nonnull
  private String getFileName(Response response) {
    return httpHelper.getFileNameFromContentDisposition(response.headers)
      .map(httpHelper::normalize)
      .orElseGet(() -> {
        log.info("Cannot extract file name from http headers. Found headers: ");
        for (Map.Entry<String, String> header : response.headers.entrySet()) {
          log.info("{}={}", header.getKey(), header.getValue());
        }

        String fileNameFromUrl = httpHelper.getFileName(response.url);
        return isNotBlank(fileNameFromUrl) ? fileNameFromUrl : downloader.randomFileName();
      });
  }

  /**
   * @return all intercepted http response (as a string) - it can be useful for debugging
   */
  @CheckReturnValue
  @Nonnull
  public String responsesAsString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Intercepted ").append(responses.size()).append(" responses:\n");

    int i = 0;
    for (Response response : responses) {
      sb.append("  #").append(++i).append("  ").append(response).append("\n");
    }
    return sb.toString();
  }

  @ParametersAreNonnullByDefault
  private static class Response {
    private final String url;
    private final int code;
    private final String reasonPhrase;
    private final String contentType;
    private final Map<String, String> headers;
    private final String content;

    private Response(String url, int code, String reasonPhrase, Map<String, String> headers,
                     String contentType, String content) {
      this.url = url;
      this.code = code;
      this.reasonPhrase = reasonPhrase;
      this.headers = headers;
      this.contentType = contentType;
      this.content = content;
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public String toString() {
      return url + " -> " + code + " \"" + reasonPhrase + "\" " + headers + " " +
          contentType + " " + " (" + content.length() + " bytes)";
    }
  }
}
