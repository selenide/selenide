package com.codeborne.selenide.conditions;

public class Readonly extends Attribute {
  public Readonly() {
    super("readonly");
  }

  @Override
  public String toString() {
    return "readonly";
  }
}
