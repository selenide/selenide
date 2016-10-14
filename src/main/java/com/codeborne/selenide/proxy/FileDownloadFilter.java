package com.codeborne.selenide.proxy;

import com.codeborne.selenide.Configuration;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class FileDownloadFilter implements ResponseFilter {
  private static final Logger log = Logger.getLogger(FileDownloadFilter.class.getName());

  private boolean active;
  private final List<File> downloadedFiles = new ArrayList<>();
  private final List<Response> responses = new ArrayList<>();
  private final Pattern patternContentDisposition = 
      Pattern.compile(".*filename\\*?=\"?([^\";]*)\"?(;charset=.*)?.*", CASE_INSENSITIVE);

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

    File file = prepareTargetFile(fileName);
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

  protected File prepareTargetFile(String fileName) {
    return new File(Configuration.reportsFolder, fileName);
  }

  String getFileName(HttpResponse response) {
    for (Map.Entry<String, String> header : response.headers().entries()) {
      String fileName = getFileNameFromContentDisposition(header.getKey(), header.getValue());
      if (fileName != null) {
        return fileName;
      }
    }

    return null;
  }

  protected String getFileNameFromContentDisposition(String headerName, String headerValue) {
    if ("Content-Disposition".equalsIgnoreCase(headerName) && headerValue != null) {
      Matcher regex = patternContentDisposition.matcher(headerValue);
      return regex.matches() ? regex.replaceFirst("$1") : null;
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
