package com.codeborne.selenide.impl;

public class DummyRandomizer extends Randomizer {
  private final String text;

  public DummyRandomizer(String text) {
    this.text = text;
  }

  @Override
  public String text() {
    return text;
  }
}
