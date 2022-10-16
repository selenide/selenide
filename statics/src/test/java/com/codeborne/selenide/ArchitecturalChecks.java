package com.codeborne.selenide;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitecturalChecks {
  private final JavaClasses selenideClasses = new ClassFileImporter().importPackages("com.codeborne", "integration");

  @Test
  void noJunitAsserts() {
    noClasses()
      .should().accessClassesThat().areAssignableTo("org.junit.jupiter.api.Assertions")
      .check(selenideClasses);
  }
}
