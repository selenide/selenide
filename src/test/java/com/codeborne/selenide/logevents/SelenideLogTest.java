package com.codeborne.selenide.logevents;

import org.junit.Test;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.IN_PROGRESS;
import static org.junit.Assert.assertEquals;

public class SelenideLogTest {

  @Test
  public void testGetSubject() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    assertEquals("Subject", log.getSubject());
  }

  @Test
  public void testGetStatus() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    assertEquals(IN_PROGRESS, log.getStatus());
  }

  @Test
  public void testGetElement() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    assertEquals("Element", log.getElement());
  }

  @Test
  public void testGetDuration() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    long duration = (0 - System.nanoTime()) / 1000000;
    assertEquals(0, duration - log.getDuration());
  }

  @Test
  public void testError() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    Throwable error = new Throwable("Error message");
    log.setError(error);
    assertEquals(error, log.getError());
  }

  @Test
  public void testToString() {
    SelenideLog log = new SelenideLog("Element", "Subject");
    assertEquals(String.format("$(%s) %s", log.getElement(), log.getSubject()), log.toString());
  }
}
