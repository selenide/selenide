package com.codeborne.selenide.impl;

import org.jspecify.annotations.Nullable;

import java.time.Duration;

@FunctionalInterface
public interface HasTimeout {
  @Nullable
  Duration timeout();
}
