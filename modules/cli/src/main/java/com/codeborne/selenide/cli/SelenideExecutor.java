package com.codeborne.selenide.cli;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Runs commands against a real browser inside the daemon, reusing {@link CommandInterpreter},
 * {@link Recorder}, and {@link CodeGenerator}. Actions are executed and recorded (a statement is
 * kept only when its command succeeds); meta commands (code/save/undo/reset) and {@code close} are
 * handled here and never recorded.
 */
final class SelenideExecutor implements CommandExecutor {
  private static final String LISTENER = "selenide-cli";

  private final SelenideDriver driver;
  private final CommandInterpreter interpreter;
  private final Recorder recorder = new Recorder();
  private final CodeGenerator codeGenerator = new CodeGenerator();

  SelenideExecutor(SelenideConfig config) {
    this.driver = new SelenideDriver(config);
    this.interpreter = new CommandInterpreter(driver);
    SelenideLogger.addListener(LISTENER, recorder);
  }

  @Override
  public Result execute(List<String> args) {
    Args parsed = Args.ofTokens(args);
    return switch (parsed.command()) {
      case "" -> Result.error("empty command");
      case "close" -> Result.shutdown("closed");
      case "code" -> Result.ok(codeGenerator.snippet(recorder.statements()));
      case "save" -> save(parsed);
      case "undo" -> undo();
      case "reset" -> reset();
      default -> runAction(parsed);
    };
  }

  private Result runAction(Args args) {
    PendingCommand command;
    try {
      command = interpreter.interpret(args);
    }
    catch (CommandException e) {
      return Result.error(e.getMessage());
    }
    recorder.expect(command.statement());
    try {
      command.action().run();
      recorder.commitIfPending();
      return Result.ok("+ " + command.statement().code());
    }
    catch (RuntimeException | AssertionError e) {
      recorder.discardPending();
      return Result.error(firstLine(e));
    }
  }

  private Result save(Args args) {
    if (args.count() < 2) {
      return Result.error("usage: save <file>");
    }
    Path file = Path.of(args.nth(1));
    try {
      Files.writeString(file, codeGenerator.snippet(recorder.statements()), UTF_8);
      return Result.ok("saved to " + file.toAbsolutePath());
    }
    catch (IOException e) {
      return Result.error("could not save: " + e.getMessage());
    }
  }

  private Result undo() {
    recorder.removeLast();
    return Result.ok("undone");
  }

  private Result reset() {
    recorder.reset();
    return Result.ok("recording cleared");
  }

  @Override
  public void shutdown() {
    SelenideLogger.removeListener(LISTENER);
    try {
      driver.close();
    }
    catch (RuntimeException e) {
      // best-effort browser shutdown
    }
  }

  private static String firstLine(Throwable e) {
    String message = e.getMessage();
    if (message == null) {
      return e.getClass().getSimpleName();
    }
    return message.strip().split("\\R", 2)[0];
  }
}
