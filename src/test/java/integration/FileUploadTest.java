package integration;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileUploadTest extends IntegrationTest {
  @Before
  public void openFileUploadForm() {
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
}
