package com.codeborne.selenide.proxy;

import com.codeborne.selenide.Configuration;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDownloadFilter implements ResponseFilter {
  private static final Logger log = Logger.getLogger(FileDownloadFilter.class.getName());

  private boolean active;
  private final List<File> downloadedFiles = new ArrayList<>();
  private final Pattern patternContentDisposition = Pattern.compile(".*filename=\"?([^\"]*)\"?.*");

  /**
   * Activate this filter.
   * Starting from this moment, it will record all responses that contain header "Content-Disposition".
   * These responses are supposed to contain a file being downloaded.
   */
  public void activate() {
    downloadedFiles.clear();
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
}
