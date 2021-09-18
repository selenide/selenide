package integration;

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
    openFile("file_upload_form.html");
  }

  @AfterEach
  void tearDown() {
    Configuration.remote = null;
    System.clearProperty("chromeoptions.mobileEmulation");
  }

  @Test
  void userCanUploadMultipleFiles() {
    File file = $("#multi-file-upload-form .file").uploadFromClasspath(
      "hello_grid_world.txt",
      "goodbye_grid_world.txt"
    );

    $("#multi-file-upload-form .submit").click();
    $("h3").shouldHave(text("Uploaded 2 files"));

    assertThat(file).exists();
    assertThat(file.getPath().replace(File.separatorChar, '/')).endsWith("/hello_grid_world.txt");

    assertThat(server.getUploadedFiles()).hasSize(2);

    assertThat(server.getUploadedFiles().get(0).getName()).endsWith("hello_grid_world.txt");
    assertThat(server.getUploadedFiles().get(1).getName()).endsWith("goodbye_grid_world.txt");

    assertThat(server.getUploadedFiles().get(0).getString()).contains("Hello, Selenium Grid!");
    assertThat(server.getUploadedFiles().get(1).getString()).contains("Goodbye, Selenium Grid!");
  }

  @Test
  void userCanUploadMultipleFiles_withoutForm() {
    openFile("file_upload_without_form.html");
    $("#fileInput").uploadFromClasspath("файл.txt", "hello_grid_world.txt", "goodbye_grid_world.txt");

    $("#uploadButton").click();
    $("h3").shouldHave(text("Uploaded 3 files").because("Actual files: " + server.getUploadedFiles()));

    assertThat(server.getUploadedFiles())
      .extracting(f -> f.getName())
      .containsExactlyInAnyOrder("файл.txt", "hello_grid_world.txt", "goodbye_grid_world.txt");

    assertThat(server.getUploadedFiles())
      .extracting(f -> f.getString("UTF-8").trim())
      .containsExactlyInAnyOrder("коромысло", "Hello, Selenium Grid!", "Goodbye, Selenium Grid!");
  }
}
