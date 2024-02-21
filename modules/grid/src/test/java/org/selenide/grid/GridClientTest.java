package org.selenide.grid;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GridClientTest {
  private final GridClient client = new GridClient("https://my.hub.com", "sid12345");

  @Test
  void parsesDownloadedFilesResponse() throws JsonProcessingException {
    String responseJson = """
      {
        "value": {
          "names": [
            ".com.google.Chrome.EoTocg",
            "hello_world.txt",
            "goodbye_world.xlsx"
          ]
        }
      }""";
    List<String> files = client.parseDownloadedFiles(responseJson).names();
    assertThat(files).containsExactly(".com.google.Chrome.EoTocg", "hello_world.txt", "goodbye_world.xlsx");
  }

  @Test
  void parsesDownloadedFilesErrorResponse() throws JsonProcessingException {
    String responseJson = """
      {
        "value": {
          "error": "unknown error",
          "message": "Cannot find downloads file system for session id: cdce9f4 — ensure downloads are enabled...",
          "stacktrace": "org.openqa.selenium.WebDriverException: Cannot find downloads file system..."
        }
      }""".trim();
    DownloadedFiles response = client.parseDownloadedFiles(responseJson);
    assertThat(response.names()).isNull();
    assertThat(response.error()).isEqualTo("unknown error");
    assertThat(response.message()).startsWith("Cannot find downloads file system");
  }

  @Test
  void parsesDownloadedFileContents() throws JsonProcessingException {
    String base64zip = "UEsDBBQACAgIAPZhVVgAAAAAAAAAAAAAAAAPAAAAaGVsbG9fd29ybGQudHh080jNycnXUQjPzAtKLFIEAFBLBwi0vbTsE" +
                       "AAAAA4AAABQSwECFAAUAAgICAD2YVVYtL207BAAAAAOAAAADwAAAAAAAAAAAAAAAAAAAAAAaGVsbG9fd29ybGQudHh0UE" +
                       "sFBgAAAAABAAEAPQAAAE0AAAAAAA==";
    String responseJson = """
      {
        "value": {
          "filename": "hello_world.txt",
          "contents": "%s"
        }
      }""".formatted(base64zip);
    FileContent response = client.parseDownloadedFile(responseJson);
    assertThat(response.error()).isNull();
    assertThat(response.message()).isNull();
    assertThat(response.stacktrace()).isNull();
    assertThat(response.filename()).isEqualTo("hello_world.txt");
    assertThat(response.contents()).startsWith("UEsDBBQACAg").endsWith("AAAA==");
  }

  @Test
  void errorResponse() throws JsonProcessingException {
    String responseJson = """
      {
        "value": {
          "error": "unknown error",
          "message": "Content-Type header is missing",
          "stacktrace": ""
        }
      }""";
    FileContent response = client.parseDownloadedFile(responseJson);
    assertThat(response.error()).isEqualTo("unknown error");
    assertThat(response.message()).isEqualTo("Content-Type header is missing");
    assertThat(response.stacktrace()).isEqualTo("");
    assertThat(response.filename()).isNull();
    assertThat(response.contents()).isNull();
  }

  @Test
  void errorResponseWithStacktrace() throws JsonProcessingException {
    String responseJson = """
      {
         "value": {
           "message": "Please specify file to download in payload as {\\"name\\": \\"fileToDownload\\"}\\nBuild info: ...",
           "stacktrace": "org.openqa.selenium.WebDriverException: Please specify file to download...",
           "error": "unknown error"
         }
      }""";
    FileContent response = client.parseDownloadedFile(responseJson);
    assertThat(response.error()).isEqualTo("unknown error");
    assertThat(response.message()).startsWith("Please specify file to download");
    assertThat(response.stacktrace()).startsWith("org.openqa.selenium.WebDriverException");
    assertThat(response.filename()).isNull();
    assertThat(response.contents()).isNull();
  }
}
