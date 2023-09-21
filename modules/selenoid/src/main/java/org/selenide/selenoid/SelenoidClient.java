package org.selenide.selenoid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public class SelenoidClient {
  private static final Logger log = LoggerFactory.getLogger(SelenoidClient.class);
  private static final ObjectMapper json = new ObjectMapper();
  final String baseUrl;
  private final String sessionId;

  public SelenoidClient(String hubUrl, String sessionId) {
    this.baseUrl = hubUrl.replace("/wd/hub", "");
    this.sessionId = sessionId;
  }

  @CheckReturnValue
  @Nonnull
  public List<String> downloads() {
    URL url = url(baseUrl + "/download/" + sessionId + "/?json");
    String fileNamesJson = readToString(url);
    List<String> fileNames = parseJson(fileNamesJson);
    log.debug("Retrieved files from {}: {}", url, fileNames);
    return fileNames;
  }

  List<String> parseJson(String fileNamesJson) {
    try {
      return json.readerForListOf(String.class).readValue(fileNamesJson);
    }
    catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse Selenoid response: " + fileNamesJson, e);
    }
  }

  @CheckReturnValue
  @Nonnull
  public File download(String fileName, File targetFolder) {
    URL url = urlOfDownloadedFile(fileName);
    try (InputStream in = connectionFromUrl(url).getInputStream()) {
      File file = new File(targetFolder, fileName);
      try (OutputStream out = Files.newOutputStream(file.toPath())) {
        IOUtils.copyLarge(in, out);
      }
      log.debug("Downloaded file from {} to {}", url, file.getAbsolutePath());
      return file;
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to download file " + url, e);
    }
  }

  public void deleteDownloadedFiles() {
    log.debug("Deleting downloaded files...");
    List<String> downloadedFiles = downloads();
    downloadedFiles.forEach(this::deleteDownloadedFile);
    log.debug("Deleted {} downloaded files", downloadedFiles.size());
  }

  public void deleteDownloadedFile(String fileName) {
    URL url = urlOfDownloadedFile(fileName);

    try {
      HttpURLConnection connection = connectionFromUrl(url);
      connection.setRequestMethod("DELETE");
      int responseCode = connection.getResponseCode();
      if (responseCode != 200) {
        throw new RuntimeException("Failed to deleted downloaded file " + fileName +
                                   ", received http status " + responseCode);
      }
      log.debug("Deleted downloaded file {}", url);
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to download file " + url, e);
    }
  }

  @CheckReturnValue
  @Nonnull
  public String getClipboardText() {
    try {
      HttpURLConnection connection = connectionFromUrl(url(baseUrl, "clipboard", sessionId));
      int code = connection.getResponseCode();
      if (code != 200)
        throw new RuntimeException("Something went wrong while getting clipboard! Response code: " + code);

      try (InputStream in = connection.getInputStream()) {
        return IOUtils.toString(in, UTF_8);
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Something went wrong while getting clipboard!", e);
    }
  }

  public void setClipboardText(String text) {
    try {
      HttpURLConnection connection = connectionFromUrl(url(baseUrl, "clipboard", sessionId));
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setConnectTimeout(10000);
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), UTF_8)) {
        writer.write(text);
      }
      int code = connection.getResponseCode();
      if (code != 200)
        throw new RuntimeException("Something went wrong while writing clipboard! Response code: " + code);
    }
    catch (IOException e) {
      throw new RuntimeException("Can't set clipboard content! ", e);
    }
  }

  @CheckReturnValue
  @Nonnull
  URL urlOfDownloadedFile(String fileName) {
    return url(baseUrl, "download", sessionId, fileName);
  }

  @CheckReturnValue
  @Nonnull
  private URL url(String url) {
    try {
      return new URL(url);
    }
    catch (MalformedURLException e) {
      throw new RuntimeException("Failed to build valid URL from " + url, e);
    }
  }

  @CheckReturnValue
  @Nonnull
  private URL url(String base, String... pathSegments) {
    try {
      return new URIBuilder(base)
        .setPathSegments(pathSegments)
        .build().toURL();
    }
    catch (URISyntaxException | MalformedURLException e) {
      throw new RuntimeException("Failed to build valid URL from " + base + '+' + Arrays.toString(pathSegments), e);
    }
  }

  @CheckReturnValue
  @Nonnull
  private String readToString(URL url) {
    try (InputStream in = connectionFromUrl(url).getInputStream()) {
      return IOUtils.toString(in, UTF_8);
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to fetch data from " + url, e);
    }
  }

  @CheckReturnValue
  @Nonnull
  private HttpURLConnection connectionFromUrl(URL url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    if (url.getUserInfo() != null) {
      String basicAuth = "Basic " + new String(Base64.getEncoder().encode(url.getUserInfo().getBytes(UTF_8)), UTF_8);
      connection.setRequestProperty("Authorization", basicAuth);
    }
    return connection;
  }

  @Override
  public String toString() {
    return "SelenoidClient{" + sessionId + "}";
  }
}
