package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static com.codeborne.selenide.Condition.text;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class FileUploadTest extends ITest {
  @BeforeEach
  void openFileUploadForm() {
    setTimeout(4000);
    if (browser().isIE()) {
      driver().close();
    }
    openFile("file_upload_form.html");
  }

  @Test
  void userCanUploadFileFromClasspath() {
    File f1 = $("#cv").uploadFromClasspath("hello_world.txt");
    File f2 = $("#avatar").uploadFromClasspath("firebug-1.11.4.xpi");
    $("#submit").click();
    $("h3").shouldHave(text("Uploaded 2 files").because("Actual files: " + server.getUploadedFiles()));

    assertThat(f1).exists();
    assertThat(f2).exists();
    assertThat(f1.getName()).isEqualTo("hello_world.txt");
    assertThat(f2.getName()).isEqualTo("firebug-1.11.4.xpi");

    assertThat(server.getUploadedFiles()).hasSize(2);
    assertThat(server.getUploadedFiles().get(0).name()).endsWith("hello_world.txt");
    assertThat(server.getUploadedFiles().get(1).name()).endsWith("firebug-1.11.4.xpi");
    assertThat(server.getUploadedFiles().get(0).content()).isEqualTo("Hello, WinRar!");
  }

  @Test
  void userCanUploadFile() throws URISyntaxException {
    File file = $("#cv").uploadFile(toLocalFile("/hello_world.txt"));
    $("#submit").click();
    $("h3").shouldHave(text("Uploaded 1 files"));
    assertThat(file).exists();
    assertThat(file.getPath()).endsWith("hello_world.txt");
    assertThat(server.getUploadedFiles().get(0).name()).endsWith("hello_world.txt");
  }

  @Test
  void userCanUploadMultipleFilesFromClasspath() {
    $("#multi-file-upload-form .file").uploadFromClasspath(
      "hello_world.txt",
      "jquery.min.js",
      "jquery-ui.min.css",
      "long_ajax_request.html",
      "page_with_alerts.html",
      "page_with_dynamic_select.html",
      "page_with_frames.html",
      "page_with_images.html",
      "selenide-logo-big.png");
    $("#multi-file-upload-form .submit").click();

    $("h3").shouldHave(text("Uploaded 9 files"));
    assertThat(server.getUploadedFiles()).hasSize(9);

    assertThat(server.getUploadedFiles().get(0).name()).endsWith("hello_world.txt");
    assertThat(server.getUploadedFiles().get(1).name()).endsWith("jquery.min.js");
    assertThat(server.getUploadedFiles().get(2).name()).endsWith("jquery-ui.min.css");
    assertThat(server.getUploadedFiles().get(3).name()).endsWith("long_ajax_request.html");
    assertThat(server.getUploadedFiles().get(8).name()).endsWith("selenide-logo-big.png");

    assertThat(server.getUploadedFiles().get(0).content()).contains("Hello, WinRar!");
    assertThat(server.getUploadedFiles().get(1).content()).contains("jQuery JavaScript Library");
    assertThat(server.getUploadedFiles().get(2).content()).contains("jQuery UI");
  }

  @Test
  void userCanUploadFilesFromClasspathInsideJar() {
    $("#multi-file-upload-form .file").uploadFromClasspath(
      "org/slf4j/Logger.class",
      "org/slf4j/LoggerFactory.class"
    );
    $("#multi-file-upload-form .submit").click();

    $("h3").shouldHave(text("Uploaded 2 files"));
    assertThat(server.getUploadedFiles()).hasSize(2);

    assertThat(server.getUploadedFiles().get(0).name()).endsWith("Logger.class");
    assertThat(server.getUploadedFiles().get(1).name()).endsWith("LoggerFactory.class");
  }

  @Test
  void userCanUploadMultipleFiles() throws URISyntaxException {
    File file = $("#multi-file-upload-form .file").uploadFile(
      toLocalFile("/hello_world.txt"),
      toLocalFile("/jquery.min.js"));

    $("#multi-file-upload-form .submit").click();
    $("h3").shouldHave(text("Uploaded 2 files"));

    assertThat(file).exists();
    assertThat(file.getPath()).endsWith("hello_world.txt");

    assertThat(server.getUploadedFiles()).hasSize(2);

    assertThat(server.getUploadedFiles().get(0).name()).endsWith("hello_world.txt");
    assertThat(server.getUploadedFiles().get(1).name()).endsWith("jquery.min.js");

    assertThat(server.getUploadedFiles().get(0).content()).contains("Hello, WinRar!");
    assertThat(server.getUploadedFiles().get(1).content()).contains("jQuery JavaScript Library v1.8.3");
  }

  @Test
  void userCanUploadMultipleFiles_withoutForm() throws URISyntaxException {
    openFile("file_upload_without_form.html");
    $("#fileInput").uploadFile(
      toLocalFile("/файл-с-русским-названием.txt"),
      toLocalFile("/hello_world.txt"),
      toLocalFile("/child_frame.txt"));

    $("#uploadButton").click();
    $("h3").shouldHave(text("Uploaded 3 files").because("Actual files: " + server.getUploadedFiles()));

    assertThat(server.getUploadedFiles())
      .extracting(f -> f.name())
      .containsExactlyInAnyOrder("файл-с-русским-названием.txt", "hello_world.txt", "child_frame.txt");

    assertThat(server.getUploadedFiles())
      .extracting(f -> f.content())
      .containsExactlyInAnyOrder("Превед медвед!", "Hello, WinRar!", "This is last frame!");
  }

  @Test
  void uploadUnexistingFile() {
    assertThatThrownBy(() ->
      $("input[type='file'][id='cv']").uploadFile(new File("src/goodbye_world.txt"))
    )
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageMatching("File not found:.*goodbye_world.txt");
  }

  @Test
  void uploadUnexistingFileFromClasspath() {
    assertThatThrownBy(() ->
      $("input[type='file'][id='cv']").uploadFromClasspath("goodbye_world.txt")
    )
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageMatching("File not found in classpath:.*goodbye_world.txt");
  }

  private File toLocalFile(String fileName) throws URISyntaxException {
    URL url = requireNonNull(getClass().getResource(fileName), () -> "Not found in classpath: " + fileName);
    return new File(url.toURI());
  }
}
