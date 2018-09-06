package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.SelenideDriver;

public class SelenideDriverFinalCleanupThread extends Thread {
  private final SelenideDriver selenideDriver;

  public SelenideDriverFinalCleanupThread(SelenideDriver selenideDriver) {
    this.selenideDriver = selenideDriver;
  }

  @Override
  public void run() {
    selenideDriver.close();
  }
}
