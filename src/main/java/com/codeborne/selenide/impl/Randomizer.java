package com.codeborne.selenide.impl;

import java.util.UUID;

public class Randomizer {
  public String text() {
    return UUID.randomUUID().toString();
  }
}
