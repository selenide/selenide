package com.codeborne.selenide.cli;

import java.util.List;

/**
 * Executes a single command (the client's argv) inside the daemon and returns a response.
 * Kept as an interface so the socket layer can be tested with a fake executor (no browser).
 */
interface CommandExecutor {
  record Result(boolean ok, boolean shutdown, String output) {
    static Result ok(String output) {
      return new Result(true, false, output);
    }

    static Result error(String output) {
      return new Result(false, false, output);
    }

    static Result shutdown(String output) {
      return new Result(true, true, output);
    }
  }

  Result execute(List<String> args);

  void shutdown();
}
