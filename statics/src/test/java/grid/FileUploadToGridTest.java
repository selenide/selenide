package grid;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

final class FileUploadToGridTest extends AbstractGridTest {
  @BeforeEach
  void openFileUploadForm() {
    Configuration.remote = "http://localhost:" + hubPort + "/wd/hub";
    Configuration.browser = "chrome";
    Configuration.headless = true;
    Configuration.proxyEnabled = true;

    openFile("file_upload_form.html");
  }

  @AfterEach
  void tearDown() {
    Configuration.remote = null;
    System.clearProperty("chromeoptions.mobileEmulation");
  }

  @Test
  void userCanUploadMultipleFiles() {
    File file = $("#multi-file-upload-form .file").uploadFile(
      new File("src/test/java/../resources/hello_world.txt"),
      new File("src/test/resources/jquery.min.js"));

    $("#multi-file-upload-form .submit").click();
    $("h3").shouldHave(text("Uploaded 2 files"));

    assertThat(file).exists();
    assertThat(file.getPath().replace(File.separatorChar, '/')).endsWith("src/test/resources/hello_world.txt");

    assertThat(server.getUploadedFiles()).hasSize(2);

    assertThat(server.getUploadedFiles().get(0).getName()).endsWith("hello_world.txt");
    assertThat(server.getUploadedFiles().get(1).getName()).endsWith("jquery.min.js");

    assertThat(server.getUploadedFiles().get(0).getString()).contains("Hello, WinRar!");
    assertThat(server.getUploadedFiles().get(1).getString()).contains("jQuery JavaScript Library v1.8.3");
  }

  @Test
  void userCanUploadMultipleFiles_withoutForm() {
    openFile("file_upload_without_form.html");
    $("#fileInput").uploadFile(
      new File("src/test/java/../resources/файл-с-русским-названием.txt"),
      new File("src/test/java/../resources/hello_world.txt"),
      new File("src/test/resources/child_frame.txt"));

    $("#uploadButton").click();
    $("h3").shouldHave(text("Uploaded 3 files").because("Actual files: " + server.getUploadedFiles()));

    assertThat(server.getUploadedFiles())
      .extracting(f -> f.getName())
      .containsExactlyInAnyOrder("файл-с-русским-названием.txt", "hello_world.txt", "child_frame.txt");

    assertThat(server.getUploadedFiles())
      .extracting(f -> f.getString("UTF-8"))
      .containsExactlyInAnyOrder("Превед медвед!", "Hello, WinRar!", "This is last frame!");
  }
}
