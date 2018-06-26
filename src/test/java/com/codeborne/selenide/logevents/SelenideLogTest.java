package com.codeborne.selenide.logevents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.IN_PROGRESS;

class SelenideLogTest {

  @Test
  void testGetSubject() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    Assertions.assertEquals("Subject", log.getSubject());
  }

  @Test
  void testGetStatus() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    Assertions.assertEquals(IN_PROGRESS, log.getStatus());
  }

  @Test
  void testGetElement() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    Assertions.assertEquals("Element", log.getElement());
  }

  @Test
  void testError() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    Throwable error = new Throwable("Error message");
    log.setError(error);
    Assertions.assertEquals(error, log.getError());
  }

  @Test
  void testToString() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    Assertions.assertEquals(String.format("$(%s) %s", log.getElement(), log.getSubject()), log.toString());
  }
}
