package com.codeborne.selenide.cli;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Per-session daemon state under {@code ~/.selenide-cli/}: each running session records the loopback
 * port its daemon listens on in {@code <session>.port}. Used to discover, connect to, and clean up
 * daemons across separate CLI invocations.
 */
final class SessionStore {
  private static final String PORT_SUFFIX = ".port";

  private SessionStore() {
  }

  static Path dir() {
    return Path.of(System.getProperty("user.home"), ".selenide-cli");
  }

  static Path logFile(String session) {
    return dir().resolve(session + ".log");
  }

  private static Path portFile(String session) {
    return dir().resolve(session + PORT_SUFFIX);
  }

  static void writePort(String session, int port) {
    try {
      Files.createDirectories(dir());
      Files.writeString(portFile(session), Integer.toString(port), UTF_8);
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static OptionalInt readPort(String session) {
    try {
      return OptionalInt.of(Integer.parseInt(Files.readString(portFile(session), UTF_8).strip()));
    }
    catch (IOException | NumberFormatException e) {
      return OptionalInt.empty();
    }
  }

  static void delete(String session) {
    try {
      Files.deleteIfExists(portFile(session));
    }
    catch (IOException e) {
      // best-effort cleanup
    }
  }

  static List<String> sessions() {
    if (!Files.isDirectory(dir())) {
      return List.of();
    }
    try (Stream<Path> files = Files.list(dir())) {
      return files
        .map(path -> path.getFileName().toString())
        .filter(name -> name.endsWith(PORT_SUFFIX))
        .map(name -> name.substring(0, name.length() - PORT_SUFFIX.length()))
        .sorted()
        .toList();
    }
    catch (IOException e) {
      return List.of();
    }
  }
}
