package com.codeborne.selenide.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.util.OptionalInt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * SessionStore.dir() depends on the "user.home" system property, which DaemonRoundTripTest and
 * DaemonClientTest temporarily redirect - since Gradle runs test classes concurrently (see
 * gradle/tests.gradle), this class must declare a read lock on it so JUnit serializes it against
 * those writers instead of racing on the JVM-wide property.
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ)
class SessionStoreTest {
  @Test
  void writesAndReadsPortForPlainSessionName() {
    String session = "unit-test-" + System.nanoTime();
    try {
      SessionStore.writePort(session, 12345);
      assertThat(SessionStore.readPort(session)).isEqualTo(OptionalInt.of(12345));
      assertThat(SessionStore.logFile(session)).isEqualTo(SessionStore.dir().resolve(session + ".log"));
    }
    finally {
      SessionStore.delete(session);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"../../../../tmp/evil", "../x", "sub/dir", "/etc/cron.d/x"})
  void rejectsSessionNamesEscapingTheSessionDirectory(String session) {
    assertThatThrownBy(() -> SessionStore.writePort(session, 12345))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining(session);
    assertThatThrownBy(() -> SessionStore.logFile(session))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void allowsSessionNameThatIsLiterallyTwoDots() {
    try {
      SessionStore.writePort("..", 12345);
      assertThat(SessionStore.readPort("..")).isEqualTo(OptionalInt.of(12345));
    }
    finally {
      SessionStore.delete("..");
    }
  }

  @Test
  void readPortReturnsEmptyForUnknownSession() {
    assertThat(SessionStore.readPort("unknown-session-" + System.nanoTime())).isEqualTo(OptionalInt.empty());
  }

  @Test
  void resolvedFileStaysDirectlyUnderSessionDir() {
    String session = "unit-test-" + System.nanoTime();
    Path resolved = SessionStore.logFile(session);
    assertThat(resolved.getParent()).isEqualTo(SessionStore.dir());
  }
}
