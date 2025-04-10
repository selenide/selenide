package integration;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AttachmentTest {
  private static final Logger log = LoggerFactory.getLogger(AttachmentTest.class);

  private File create(String name) throws IOException {
    File file = new File("build/" + name + ".txt");
    FileUtils.writeStringToFile(file, "Hello, " + name, UTF_8);
    return file;
  }

  @Test
  void one() throws IOException {
    File file = create("one");
    System.out.printf("[[ATTACHMENT|%s]]%n", file.getAbsolutePath());
  }

  @Test
  void two() throws IOException {
    File file = create("two");
    System.out.printf("SHOT [[ATTACHMENT|%s]] SAVED%n", file.getAbsolutePath());
  }

  @Test
  void three() throws IOException {
    File file = create("three");

    log.info("[[ATTACHMENT|{}]]", file.getAbsolutePath());
  }

  @Test
  void four() throws IOException {
    File file = create("four");

    log.info("SHOT [[ATTACHMENT|{}]] SAVED", file.getAbsolutePath());
  }

  @AfterEach
  void tearDown() {
//    Assertions.fail("zopa!");
  }
}
