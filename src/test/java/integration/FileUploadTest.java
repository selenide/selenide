package integration;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class FileUploadTest extends IntegrationTest {
  @Before
  public void openFileUploadForm() {
    assumeFalse(isPhantomjs());

    if (isIE()) {
      closeWebDriver();
    }
    openFile("file_upload_form.html");
    server.uploadedFiles.clear();
  }

  @Test
  public void userCanUploadFileFromClasspath() {
    File f1 = $("#cv").uploadFromClasspath("hello_world.txt");
    File f2 = $("#avatar").uploadFromClasspath("firebug-1.11.4.xpi");
    $("#submit").click();
    
    assertTrue(f1.exists());
    assertTrue(f2.exists());
    assertEquals("hello_world.txt", f1.getName());
    assertEquals("firebug-1.11.4.xpi", f2.getName());

    assertEquals(2, server.uploadedFiles.size());
    assertTrue(server.uploadedFiles.get(0).getName().endsWith("hello_world.txt"));
    assertTrue(server.uploadedFiles.get(1).getName().endsWith("firebug-1.11.4.xpi"));
    
    assertEquals("Hello, WinRar!", server.uploadedFiles.get(0).getString());
  }

  @Test
  public void userCanUploadFile() {
    File file = $("#cv").uploadFile(new File("src/test/java/../resources/hello_world.txt"));
    $("#submit").click();
    assertTrue(file.exists());
    assertTrue(file.getPath().replace(File.separatorChar, '/').endsWith("src/test/resources/hello_world.txt"));
    assertTrue(server.uploadedFiles.get(0).getName().endsWith("hello_world.txt"));
  }

  @Test
  public void userCanUploadMultipleFilesFromClasspath() {
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

    assertEquals(9, server.uploadedFiles.size());
    
    assertTrue(server.uploadedFiles.get(0).getName().endsWith("hello_world.txt"));
    assertTrue(server.uploadedFiles.get(1).getName().endsWith("jquery.min.js"));
    assertTrue(server.uploadedFiles.get(2).getName().endsWith("jquery-ui.min.css"));
    assertTrue(server.uploadedFiles.get(3).getName().endsWith("long_ajax_request.html"));
    assertTrue(server.uploadedFiles.get(8).getName().endsWith("selenide-logo-big.png"));
    
    assertTrue(server.uploadedFiles.get(0).getString().contains("Hello, WinRar!"));
    assertTrue(server.uploadedFiles.get(1).getString().contains("jQuery JavaScript Library"));
    assertTrue(server.uploadedFiles.get(2).getString().contains("jQuery UI"));
  }

  @Test
  public void userCanUploadMultipleFiles() {
    File file = $("#multi-file-upload-form .file").uploadFile(
        new File("src/test/java/../resources/hello_world.txt"), 
        new File("src/test/resources/jquery.min.js"));
    
    $("#multi-file-upload-form .submit").click();

    assertTrue(file.exists());
    assertTrue(file.getPath().replace(File.separatorChar, '/').endsWith("src/test/resources/hello_world.txt"));

    assertEquals(2, server.uploadedFiles.size());

    assertTrue(server.uploadedFiles.get(0).getName().endsWith("hello_world.txt"));
    assertTrue(server.uploadedFiles.get(1).getName().endsWith("jquery.min.js"));

    assertTrue(server.uploadedFiles.get(0).getString().contains("Hello, WinRar!"));
    assertTrue(server.uploadedFiles.get(1).getString().contains("jQuery JavaScript Library v1.8.3"));
  }
}
