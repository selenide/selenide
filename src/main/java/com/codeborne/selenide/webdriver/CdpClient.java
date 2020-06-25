package com.codeborne.selenide.webdriver;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import static java.util.regex.Matcher.quoteReplacement;

@ParametersAreNonnullByDefault
public class CdpClient {
  private static final Logger log = LoggerFactory.getLogger(CdpClient.class);
  private static final Pattern REGEX_ENCODE_FOR_JSON = Pattern.compile("\"", Pattern.LITERAL);

  public void setDownloadsFolder(DriverService driverService, RemoteWebDriver driver, String downloadsFolder) {
    setDownloadsFolder(driverService.getUrl(), driver.getSessionId(), downloadsFolder);
  }

  public void setDownloadsFolder(URL remoteDriverUrl, SessionId driverSessionId, String downloadsFolder) {
    try {
      String command = command(downloadsFolder);
      post(remoteDriverUrl, driverSessionId, command);
      log.info("Downloading files to {}", downloadsFolder);
    }
    catch (IOException e) {
      String message = String.format("Failed to set downloads folder to %s", downloadsFolder);
      throw new RuntimeException(message, e);
    }
  }

  @CheckReturnValue
  @Nonnull
  private String command(String downloadsFolder) {
    return "{" +
        "  \"cmd\": \"Page.setDownloadBehavior\",\n" +
        "  \"params\": {\n" +
        "    \"behavior\": \"allow\", \n" +
        "    \"downloadPath\": \"" + escapeForJson(downloadsFolder) + "\"\n" +
        "  }\n" +
        "}";
  }

  String escapeForJson(String text) {
    return REGEX_ENCODE_FOR_JSON.matcher(text).replaceAll(quoteReplacement("\\\""));
  }

  @CheckReturnValue
  @Nonnull
  private HttpPost request(String url, String command) {
    HttpPost request = new HttpPost(url);
    request.addHeader("Content-Type", "application/json");
    request.setEntity(new StringEntity(command));
    return request;
  }

  private void post(URL remoteDriverUrl, SessionId driverSessionId, String command) throws IOException {
    String url = String.format("%s/session/%s/chromium/send_command", remoteDriverUrl, driverSessionId);
    HttpPost request = request(url, command);
    try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
      HttpResponse response = httpClient.execute(request);
      int code = response.getCode();
      if (code != 200) {
        String error = response.getReasonPhrase();
        String message = String.format("Failed to send CDP command %s: status=%s, error=%s", command, code, error);
        throw new IOException(message);
      }
    }
  }
}
