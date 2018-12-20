package com.codeborne.selenide.proxy;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.Downloader;
import com.codeborne.selenide.impl.HttpHelper;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDownloadFilter implements ResponseFilter {
  private static final Logger log = Logger.getLogger(FileDownloadFilter.class.getName());
  private final Config config;
  private final Downloader downloader;

  private HttpHelper httpHelper = new HttpHelper();
  private boolean active;
  private final List<File> downloadedFiles = new ArrayList<>();
  private final List<Response> responses = new ArrayList<>();

  public FileDownloadFilter(Config config) {
    this(config, new Downloader());
  }

  FileDownloadFilter(Config config, Downloader downloader) {
    this.config = config;
    this.downloader = downloader;
  }

  /**
   * Activate this filter.
   * Starting from this moment, it will record all responses that contain header "Content-Disposition".
   * These responses are supposed to contain a file being downloaded.
   */
  public void activate() {
    downloadedFiles.clear();
    responses.clear();
    active = true;
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
    responses.add(new Response(messageInfo.getUrl(),
        response.getStatus().code(),
        response.getStatus().reasonPhrase(),
        toMap(response.headers()),
        contents.getContentType(),
        contents.getTextContents()
    ));

    if (response.getStatus().code() < 200 || response.getStatus().code() >= 300) return;

    String fileName = getFileName(response);
    if (fileName == null) return;

    File file = downloader.prepareTargetFile(config, fileName);
    try {
      FileUtils.writeByteArrayToFile(file, contents.getBinaryContents());
      downloadedFiles.add(file);
    }
    catch (IOException e) {
      log.log(Level.SEVERE, "Failed to save downloaded file to " + file.getAbsolutePath() +
          " for url " + messageInfo.getUrl(), e);
    }
  }

  private Map<String, String> toMap(HttpHeaders headers) {
    Map<String, String> map = new HashMap<>();
    for (Map.Entry<String, String> header : headers) {
      map.put(header.getKey(), header.getValue());
    }
    return map;
  }

  /**
   * @return list of all downloaded files since activation.
   */
  public List<File> getDownloadedFiles() {
    return downloadedFiles;
  }

  String getFileName(HttpResponse response) {
    for (Map.Entry<String, String> header : response.headers().entries()) {
      Optional<String> fileName = httpHelper.getFileNameFromContentDisposition(header.getKey(), header.getValue());
      if (fileName.isPresent()) {
        return fileName.get();
      }
    }

    return null;
  }

  /**
   * @return all intercepted http response (as a string) - it can be useful for debugging
   */
  public String getResponses() {
    StringBuilder sb = new StringBuilder();
    sb.append("Intercepted ").append(responses.size()).append(" responses.");

    for (Response response : responses) {
      sb.append("\n  ").append(response).append("\n");
    }
    return sb.toString();
  }

  private static class Response {
    private String url;
    private int code;
    private String reasonPhrase;
    private String contentType;
    private Map<String, String> headers;
    private String content;

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
    public String toString() {
      return url + " -> " + code + " \"" + reasonPhrase + "\" " + headers + " " +
          contentType + " " + " (" + content.length() + " bytes)";
    }
  }
}
