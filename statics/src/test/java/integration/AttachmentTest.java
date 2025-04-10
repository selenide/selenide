package integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AttachmentTest {
  private static final Logger log = LoggerFactory.getLogger(AttachmentTest.class);

  @Test
  void one() {
    System.out.printf("[[ATTACHMENT|%s]]%n", new File("build.gradle").getAbsolutePath());
    System.out.printf("[[ATTACHMENT|%s]]%n", new File("CHANGELOG.md").getPath());
  }

  @Test
  void two() {
    System.out.printf("SHOT [[ATTACHMENT|%s]] SAVED%n", new File("LICENSE").getAbsolutePath());
    System.out.printf("SHOT [[ATTACHMENT|%s]] SAVED%n", new File("README.md").getPath());
  }

  @Test
  void three() {
    log.info("[[ATTACHMENT|{}]]", new File("release").getAbsolutePath());
  }

  @Test
  void four() {
    log.info("SHOT [[ATTACHMENT|{}]] SAVED", new File("settings.gradle").getAbsolutePath());
  }

  @AfterEach
  void tearDown() {
    Assertions.fail("zopa!");
  }
}
