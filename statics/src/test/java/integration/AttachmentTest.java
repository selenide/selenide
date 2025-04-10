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
    System.out.printf("[[ATTACHMENT|%s]]%n", new File("build.gradle").getAbsolutePath()); // ok
    System.out.printf("[[ATTACHMENT|%s]]%n", new File("CHANGELOG.md").getAbsolutePath()); // ok
  }

  @Test
  void two() {
    System.out.printf("SHOT [[ATTACHMENT|%s]] SAVED%n", new File("LICENSE").getAbsolutePath()); // NOK: /Users/andrei/.jenkins/workspace/selenide/statics/LICENSE
    System.out.printf("SHOT [[ATTACHMENT|%s]] SAVED%n", new File("README.md").getAbsolutePath()); // ok
  }

  @Test
  void three() {
    log.info("[[ATTACHMENT|{}]]", new File("release").getAbsolutePath()); // NOK /Users/andrei/.jenkins/workspace/selenide/statics/release
  }

  @Test
  void four() {
    log.info("SHOT [[ATTACHMENT|{}]] SAVED", new File("settings.gradle").getAbsolutePath()); // NOK: /Users/andrei/.jenkins/workspace/selenide/statics/settings.gradle
  }

  @AfterEach
  void tearDown() {
    Assertions.fail("zopa!");
  }
}
