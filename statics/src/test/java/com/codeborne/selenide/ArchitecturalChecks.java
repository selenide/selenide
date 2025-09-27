package com.codeborne.selenide;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class ArchitecturalChecks {
  private final JavaClasses selenideClasses = new ClassFileImporter().importPackages("com.codeborne", "integration");

  @Test
  void noJunitAsserts() {
    noClasses()
      .should().accessClassesThat().areAssignableTo("org.junit.jupiter.api.Assertions")
      .check(selenideClasses);
  }

  @Test
  void noJunit5DisabledTests() {
    noClasses()
      .that().areNotAssignableTo("integration.AutoCompleteTest")
      .should().beAnnotatedWith("org.junit.jupiter.api.Disabled")
      .check(selenideClasses);

    noMethods()
      .should().beAnnotatedWith("org.junit.jupiter.api.Disabled")
      .check(selenideClasses);
  }

  @Test
  void noJunitAssumptions() {
    noClasses()
      .should().accessClassesThat().areAssignableTo("org.junit.jupiter.api.Assumptions")
      .check(selenideClasses);
  }

  @Test
  void noJunit4DisabledTests() {
    noClasses()
      .should().beAnnotatedWith("org.junit.Ignore")
      .check(selenideClasses);

    noMethods()
      .should().beAnnotatedWith("org.junit.Ignore")
      .check(selenideClasses);
  }

  @Test
  void allFieldsInExceptionsShouldBeFinal() {
    fields()
      .that().areDeclaredInClassesThat().areAssignableTo(Throwable.class)
      .and().doNotHaveName("timeoutMs")
      .and().doNotHaveName("screenshot")
      .and().doNotHaveName("detailedErrorMessage")
      .should().beFinal()
      .check(selenideClasses);
  }
}
