package com.codeborne.selenide.drivercommands;

public class SelenideDriverFinalCleanupThread extends Thread {
  private final LazyDriver driver;

  SelenideDriverFinalCleanupThread(LazyDriver driver) {
    this.driver = driver;
  }

  @Override
  public void run() {
    driver.close();
  }
}
