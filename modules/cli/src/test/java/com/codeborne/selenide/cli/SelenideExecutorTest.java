package com.codeborne.selenide.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelenideExecutorTest {
  @TempDir
  private Path base;

  @Test
  void allowsAPlainFileInTheWorkingDirectory() {
    assertThat(SelenideExecutor.resolveWithin(base, "Login.java"))
      .isEqualTo(base.resolve("Login.java"));
  }

  @Test
  void allowsANestedFileWithinTheWorkingDirectory() {
    assertThat(SelenideExecutor.resolveWithin(base, "src/test/java/LoginTest.java"))
      .isEqualTo(base.resolve("src/test/java/LoginTest.java"));
  }

  @Test
  void rejectsParentDirectoryTraversal() {
    assertThatThrownBy(() -> SelenideExecutor.resolveWithin(base, "../../.ssh/authorized_keys"))
      .isInstanceOf(CommandException.class)
      .hasMessageContaining("outside the working directory");
  }

  @Test
  void rejectsAnAbsolutePathOutsideTheWorkingDirectory() {
    String absoluteOutside = base.resolveSibling("evil").resolve("cron").toString();
    assertThatThrownBy(() -> SelenideExecutor.resolveWithin(base, absoluteOutside))
      .isInstanceOf(CommandException.class)
      .hasMessageContaining("outside the working directory");
  }
}
