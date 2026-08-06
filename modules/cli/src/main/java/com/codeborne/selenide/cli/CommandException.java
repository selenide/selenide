package com.codeborne.selenide.cli;

/**
 * Thrown when a REPL line cannot be parsed into a valid Selenide command.
 */
class CommandException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  CommandException(String message) {
    super(message);
  }
}
