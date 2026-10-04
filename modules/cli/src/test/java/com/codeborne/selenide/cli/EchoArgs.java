package com.codeborne.selenide.cli;

import java.io.PrintStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Prints each command-line argument on its own line. Used by {@link DaemonClientTest} as a subprocess
 * to check how arguments survive being passed to a child process.
 */
public final class EchoArgs {
  private EchoArgs() {
  }

  public static void main(String[] args) {
    PrintStream out = new PrintStream(System.out, true, UTF_8);
    for (String arg : args) {
      out.println(arg);
    }
  }
}
