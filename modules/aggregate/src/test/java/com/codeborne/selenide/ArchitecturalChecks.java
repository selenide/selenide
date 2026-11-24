package com.codeborne.selenide;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class ArchitecturalChecks {
  private final JavaClasses selenideClasses = new ClassFileImporter()
    .importPackages("com.codeborne.selenide", "org.selenide", "integration", "it");

  @Test
  void noJunitAsserts() {
    noClasses()
      .should().accessClassesThat().areAssignableTo("org.junit.jupiter.api.Assertions")
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

  @Test
  void noAssertjContentWithoutEncoding() {
    noClasses()
      .should().callMethodWhere(assertjContent())
      .because("AssertJ's content() uses system default encoding; method content(UTF_8) should be used instead")
      .check(selenideClasses);
  }

  private DescribedPredicate<JavaMethodCall> assertjContent() {
    return describe("AssertJ method content() without parameters", call ->
      "content".equals(call.getName())
        && call.getTarget().getRawParameterTypes().isEmpty()
        && call.getTargetOwner().getPackageName().startsWith("org.assertj"));
  }
}
