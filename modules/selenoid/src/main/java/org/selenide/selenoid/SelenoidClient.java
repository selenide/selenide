package org.selenide.selenoid;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public class SelenoidClient {
  private static final Logger log = LoggerFactory.getLogger(SelenoidClient.class);
  private static final Type listType = new TypeToken<List<String>>() {}.getType();
  private static final Gson gson = new Gson();
  final String baseUrl;
  private final String sessionId;

  public SelenoidClient(String hubUrl, String sessionId) {
    if (!hubUrl.endsWith("/wd/hub")) {
      throw new IllegalArgumentException("Expect hub url to end with /wd/hub, but received: " + hubUrl);
    }
    this.baseUrl = hubUrl.replace("/wd/hub", "");
    this.sessionId = sessionId;
  }

  @CheckReturnValue
  @Nonnull
  public List<String> downloads() {
    URL url = url(baseUrl + "/download/" + sessionId + "/?json");
    String fileNamesJson = readToString(url);
    List<String> fileNames = gson.fromJson(fileNamesJson, listType);
    log.debug("Retrieved files from {}: {}", url, fileNames);
    return fileNames;
  }

  @CheckReturnValue
  @Nonnull
  public File download(String fileName) {
    URL url = urlOfDownloadedFile(fileName);
    try (InputStream in = url.openStream()) {
      Path uniqueDir = Files.createTempDirectory("selenoid-download");
      File file = new File(uniqueDir.toFile(), fileName);
      try (OutputStream out = new FileOutputStream(file)) {
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
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
      throw new RuntimeException("Failed to build valid URL from " + url);
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
      throw new RuntimeException("Failed to build valid URL from " + base + '+' + Arrays.toString(pathSegments));
    }
  }

  @CheckReturnValue
  @Nonnull
  private String readToString(URL url) {
    try {
      return IOUtils.toString(url, UTF_8);
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to fetch data from " + url);
    }
  }
}
