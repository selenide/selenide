package com.codeborne.selenide.impl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.BiDi;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.log.GenericLogEntry;
import org.openqa.selenium.bidi.log.Log;
import org.openqa.selenium.bidi.log.LogLevel;
import org.openqa.selenium.bidi.log.StackFrame;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.http.ConnectionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public class BiDiUti {
  private static final Logger log = LoggerFactory.getLogger(BiDiUti.class);

  public static boolean isBiDiEnabled(WebDriver webDriver) {
    try {
      return webDriver instanceof HasBiDi hasBiDi && hasBiDi.getBiDi() != null;
    }
    catch (BiDiException notEnabled) {
      return false;
    }
  }

  public static Optional<BiDi> getBiDiIfEnabled(WebDriver webDriver) {
    try {
      return webDriver instanceof HasBiDi hasBiDi ? Optional.of(hasBiDi.getBiDi()) : Optional.empty();
    }
    catch (BiDiException | ConnectionFailedException notEnabled) {
      return Optional.empty();
    }
  }

  public static List<LogEntry> collectBrowserLogs(WebDriver webDriver) {
    return getBiDiIfEnabled(webDriver).map((BiDi biDi) -> {
      List<LogEntry> logs = new CopyOnWriteArrayList<>();
      biDi.addListener(Log.entryAdded(), (org.openqa.selenium.bidi.log.LogEntry logEntry) -> {
        log.trace("Received browser log {}", logEntry);
        logs.addAll(readBiDiLogEntry(logEntry));
      });
      return logs;
    }).orElse(emptyList());
  }

  private static List<LogEntry> readBiDiLogEntry(org.openqa.selenium.bidi.log.LogEntry logEntry) {
    return Stream.of(
        logEntry.getConsoleLogEntry(),
        logEntry.getGenericLogEntry(),
        logEntry.getJavascriptLogEntry()
      ).flatMap(Optional::stream)
      .map(entry -> readBiDiLogEntry(entry))
      .toList();
  }

  private static LogEntry readBiDiLogEntry(GenericLogEntry log) {
    return new LogEntry(readBiDiLogLevel(log.getLevel()), log.getTimestamp(), readBiDiLogMessage(log));
  }

  private static String readBiDiLogMessage(GenericLogEntry log) {
    if (log.getStackTrace() == null || log.getStackTrace().getCallFrames().isEmpty()) {
      return log.getText();
    }
    StackFrame frame = log.getStackTrace().getCallFrames().get(0);
    return "%s %s:%s %s".formatted(frame.getUrl(), frame.getLineNumber(), frame.getColumnNumber(), log.getText());
  }

  private static Level readBiDiLogLevel(LogLevel level) {
    return switch (level) {
      case DEBUG -> Level.FINE;
      case INFO -> Level.INFO;
      case WARNING -> Level.WARNING;
      case ERROR -> Level.SEVERE;
    };
  }
}
