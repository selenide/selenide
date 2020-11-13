package com.codeborne.selenide.impl;

class DummyClock extends Clock {
  private final long currentTime;

  DummyClock(long currentTime) {
    this.currentTime = currentTime;
  }

  @Override
  public long timestamp() {
    return currentTime;
  }
}
