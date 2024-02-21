package org.selenide.grid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.io.Zip;
import org.openqa.selenium.remote.http.ClientConfig;
import org.openqa.selenium.remote.http.Contents;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Supplier;

import static java.time.Duration.ofSeconds;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.openqa.selenium.json.Json.JSON_UTF_8;
import static org.openqa.selenium.remote.http.HttpClient.Factory.createDefault;
import static org.openqa.selenium.remote.http.HttpMethod.DELETE;
import static org.openqa.selenium.remote.http.HttpMethod.GET;
import static org.openqa.selenium.remote.http.HttpMethod.POST;

@ParametersAreNonnullByDefault
public class GridClient {
  private static final Logger log = LoggerFactory.getLogger(GridClient.class);
  private static final ObjectMapper json = new ObjectMapper();
  private final HttpClient.Factory httpClientFactory = createDefault();
  private final ClientConfig config;

  final String baseUrl;
  private final String sessionId;

  public GridClient(String hubUrl, String sessionId) {
    this.baseUrl = hubUrl.replace("/wd/hub", "");
    this.sessionId = sessionId;
    config = ClientConfig.defaultConfig().readTimeout(ofSeconds(10)).baseUrl(url());
  }

  @Nonnull
  @CheckReturnValue
  private URL url() {
    try {
      return new URL(baseUrl);
    }
    catch (MalformedURLException e) {
      throw new RuntimeException("Failed to parse url " + baseUrl, e);
    }
  }

  @CheckReturnValue
  @Nonnull
  public List<String> downloads() {
    try (HttpClient client = httpClientFactory.createClient(config)) {
      String uri = "%s/session/%s/se/files".formatted(baseUrl, sessionId);
      HttpRequest request = new HttpRequest(GET, uri);
      HttpResponse response = client.execute(request);
      List<String> fileNames = verifyNoErrors(parseDownloadedFiles(Contents.string(response))).names();
      log.debug("Retrieved files from {}: {}", uri, fileNames);
      return fileNames;
    }
    catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  DownloadedFiles parseDownloadedFiles(String responseJson) throws JsonProcessingException {
    DownloadedFilesResponse downloadedFiles = json.readValue(responseJson, DownloadedFilesResponse.class);
    return downloadedFiles.value();
  }

  private <T extends GridResponse> T verifyNoErrors(T response) {
    if (isNotBlank(response.error())) {
      throw new RuntimeException("%s: %s".formatted(response.error(), response.message()));
    }
    return response;
  }

  @CheckReturnValue
  @Nonnull
  public File download(String fileName, File targetFolder) {
    String uri = "%s/session/%s/se/files".formatted(baseUrl, sessionId);
    try (HttpClient client = httpClientFactory.createClient(config)) {
      HttpRequest request = new HttpRequest(POST, uri)
        .addHeader("Content-Type", JSON_UTF_8)
        .setContent(toJson(new FileRequest(fileName)));
      HttpResponse response = client.execute(request);
      FileContent fileContent = verifyNoErrors(parseDownloadedFile(Contents.string(response)));
      Zip.unzip(fileContent.contents(), targetFolder);
      File file = new File(targetFolder, fileName);
      log.debug("Downloaded file from {} to {}", uri, file.getAbsolutePath());
      return file;
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to download file " + uri, e);
    }
  }

  @Nonnull
  private Supplier<InputStream> toJson(Object content) {
    return () -> {
      try {
        return new ByteArrayInputStream(json.writeValueAsBytes(content));
      }
      catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to convert %s to json".formatted(content), e);
      }
    };
  }

  FileContent parseDownloadedFile(String responseJson) throws JsonProcessingException {
    return json.readValue(responseJson, FileContentResponse.class).value();
  }

  public void deleteDownloadedFiles() {
    log.debug("Deleting downloaded files...");
    String uri = "%s/session/%s/se/files".formatted(baseUrl, sessionId);
    try (HttpClient client = httpClientFactory.createClient(config)) {
      HttpRequest request = new HttpRequest(DELETE, uri);
      HttpResponse response = client.execute(request);
      String responseJson = Contents.string(response);
      log.debug("Deleted downloaded files by {}: {}", uri, responseJson);
    }
  }

  @Override
  public String toString() {
    return "GridClient{" + sessionId + "}";
  }
}
